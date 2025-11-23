package my.ep.web.util.sm.helper;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;

import my.ep.biz.service.pm.authorization.user.IUserSO;
import my.ep.biz.service.pm.common.categorycode.ICategoryCodeSO;
import my.ep.biz.service.pm.common.certbody.ICertBodySO;
import my.ep.biz.service.pm.common.city.ICitySO;
import my.ep.biz.service.pm.common.country.ICountrySO;
import my.ep.biz.service.pm.common.district.IDistrictSO;
import my.ep.biz.service.pm.common.division.IDivisionSO;
import my.ep.biz.service.pm.common.educationfield.IEducationFieldSO;
import my.ep.biz.service.pm.common.kpi.IPmKpiSO;
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
import my.ep.biz.service.sm.search.ISupplierCategorySO;
import my.ep.biz.service.sm.search.ISupplierSearchSO;
import my.ep.biz.service.sm.siteVisit.ISiteAssignSO;
import my.ep.biz.service.sm.supplierDataMaintenance.ISupplierDataMaintenanceSO;
import my.ep.biz.service.sm.workFlowStatus.ISmWorkFlowStatusSO;
import my.ep.bpm.vo.WorklistItemVO;
import my.ep.constant.EpPropertiesConst;
import my.ep.constant.MessageConst;
import my.ep.constant.ModuleCodeConst;
import my.ep.constant.pm.ParameterConst;
import my.ep.constant.pm.ParameterTypeConst;
import my.ep.constants.CommonConstants;
import my.ep.dto.pm.authorization.role.RoleReqDTO;
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
import my.ep.dto.pm.common.division.DivisionReqDTO;
import my.ep.dto.pm.common.division.DivisionResDTO;
import my.ep.dto.pm.common.document.DocumentReqDTO;
import my.ep.dto.pm.common.educationfield.EducationFieldReqDTO;
import my.ep.dto.pm.common.educationfield.EducationFieldResDTO;
import my.ep.dto.pm.common.kpi.KpiReqDTO;
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
import my.ep.dto.sm.bulkApplication.SmBulkApplOverlapCatVO;
import my.ep.dto.sm.common.myidentity.MyIdentityReqDTO;
import my.ep.dto.sm.common.myidentity.MyIdentityResDTO;
import my.ep.dto.sm.gst.SupplierGstReqDTO;
import my.ep.dto.sm.mofRegistration.MofRegistrationReqDTO;
import my.ep.dto.sm.mofRegistration.MofRegistrationResDTO;
import my.ep.dto.sm.mofRegistration.SmTrackingDairyVO;
import my.ep.dto.sm.personnel.PersonnelReqDTO;
import my.ep.dto.sm.personnel.PersonnelResDTO;
import my.ep.dto.sm.reviewApplication.ReviewApplicationReqDTO;
import my.ep.dto.sm.revision.SmApplRevisionReqDTO;
import my.ep.dto.sm.revision.SmApplRevisionResDTO;
import my.ep.dto.sm.search.SupplierCategoryReqDTO;
import my.ep.dto.sm.search.SupplierCategoryResDTO;
import my.ep.dto.sm.search.SupplierSearchReqDTO;
import my.ep.dto.sm.search.SupplierSearchResDTO;
import my.ep.dto.sm.siteVisit.SiteAssignReqDTO;
import my.ep.dto.sm.siteVisit.SiteAssignResDTO;
import my.ep.dto.sm.smRemarks.SmRemaksResDTO;
import my.ep.dto.sm.smRemarks.SmRemarksReqDTO;
import my.ep.dto.sm.sst.SupplierSstReqDTO;
import my.ep.dto.sm.supplierDataMaintenance.SupplierDataMaintenanceReqDTO;
import my.ep.dto.sm.supplierDataMaintenance.SupplierDataMaintenanceResDTO;
import my.ep.dto.sm.virtualcert.VirtualCertReqDTO;
import my.ep.dto.sm.virtualcert.VirtualCertResDTO;
import my.ep.dto.sm.workFlowStatus.SmWorkFlowStatusReqDTO;
import my.ep.dto.sm.workFlowStatus.SmWorkFlowStatusResDTO;
import my.ep.exception.EpException;
import my.ep.helper.EntityHelper;
import my.ep.persistence.constant.DbValueConst;
import my.ep.persistence.entity.BaseEntity;
import my.ep.persistence.entity.pm.authorization.PmRole;
import my.ep.persistence.entity.pm.authorization.PmRoleDesc;
import my.ep.persistence.entity.pm.authorization.PmUser;
import my.ep.persistence.entity.pm.authorization.PmUserOrg;
import my.ep.persistence.entity.pm.authorization.PmUserRole;
import my.ep.persistence.entity.pm.common.PmCategoryL1;
import my.ep.persistence.entity.pm.common.PmCategoryL2;
import my.ep.persistence.entity.pm.common.PmCategoryL3;
import my.ep.persistence.entity.pm.common.PmCertBody;
import my.ep.persistence.entity.pm.common.PmCity;
import my.ep.persistence.entity.pm.common.PmCountry;
import my.ep.persistence.entity.pm.common.PmDistrict;
import my.ep.persistence.entity.pm.common.PmDivision;
import my.ep.persistence.entity.pm.common.PmDocument;
import my.ep.persistence.entity.pm.common.PmEduField;
import my.ep.persistence.entity.pm.common.PmKPIValue;
import my.ep.persistence.entity.pm.common.PmMachine;
import my.ep.persistence.entity.pm.common.PmParameter;
import my.ep.persistence.entity.pm.common.PmParameterDesc;
import my.ep.persistence.entity.pm.common.PmRace;
import my.ep.persistence.entity.pm.common.PmReasonDesc;
import my.ep.persistence.entity.pm.common.PmRegStatus;
import my.ep.persistence.entity.pm.common.PmSalutation;
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
import my.ep.persistence.entity.sm.SmCategoryOverlapDoc;
import my.ep.persistence.entity.sm.SmChequeSignatory;
import my.ep.persistence.entity.sm.SmCompanyAddress;
import my.ep.persistence.entity.sm.SmCompanyBasic;
import my.ep.persistence.entity.sm.SmDisciplinaryAction;
import my.ep.persistence.entity.sm.SmDisciplinaryActionRemarks;
import my.ep.persistence.entity.sm.SmEquityCompany;
import my.ep.persistence.entity.sm.SmFacility;
import my.ep.persistence.entity.sm.SmGstSupplier;
import my.ep.persistence.entity.sm.SmGstSupplierDoc;
import my.ep.persistence.entity.sm.SmMachineCategory;
import my.ep.persistence.entity.sm.SmMachineDtl;
import my.ep.persistence.entity.sm.SmManufacturingDtl;
import my.ep.persistence.entity.sm.SmMofAccount;
import my.ep.persistence.entity.sm.SmMofCert;
import my.ep.persistence.entity.sm.SmPersonnel;
import my.ep.persistence.entity.sm.SmPersonnelAddress;
import my.ep.persistence.entity.sm.SmRelatedCompany;
import my.ep.persistence.entity.sm.SmRemark;
import my.ep.persistence.entity.sm.SmSoftcertRequest;
import my.ep.persistence.entity.sm.SmSstSupplier;
import my.ep.persistence.entity.sm.SmStaffAcademic;
import my.ep.persistence.entity.sm.SmStaffExperience;
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
import my.ep.persistence.entity.sm.SmSvOffice;
import my.ep.persistence.entity.sm.SmSvTask;
import my.ep.persistence.entity.sm.SmSvTaskGroup;
import my.ep.persistence.entity.sm.SmSvTemplate;
import my.ep.persistence.entity.sm.SmWorkflowStatus;
import my.ep.type.pm.RoleCode;
import my.ep.util.EpPropertiesUtils;
import my.ep.util.MessageUtils;
import my.ep.util.ResourceBundleUtils;
import my.ep.web.backingbean.pm.cc.CategoryCodeSearchBackingBean;
import my.ep.web.backingbean.sm.registration.RegistrationBackingBean;
import my.ep.web.constant.sm.SupplierManagementConst;
import my.ep.web.managedbean.sm.SmApplicationManagedBean;
import my.ep.web.util.AddressUtils;
import my.ep.web.util.DateUtils;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.lookupservice.BusinessDelegate;
import my.ep.web.util.pm.helper.ParameterHelper;
import my.ep.web.util.pm.helper.PmCommonUtil;
import my.ep.web.vo.DisciplinaryActionCategoryVO;
import my.ep.web.vo.SmApplStatusInfoVO;
import my.ep.web.vo.SmAuthorizedPersonnelCountVO;
import my.ep.web.vo.SmLicenseInfoVO;
import my.ep.web.vo.SmMachineCategoryVO;
import my.ep.web.vo.SmOverlapCategoryVO;
import my.ep.web.vo.SmSiteVisitFacilityVO;
import my.ep.web.vo.SmSupplierBasicInfoVO;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang.SerializationUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.velocity.VelocityContext;
import org.primefaces.context.RequestContext;

import com.liferay.portal.kernel.util.StringUtil;


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
	 * Set personnel authorization count
	 * 
	 * @param SmAppl
	 *            appl
	 * @return SmAuthorizedPersonnelCountVO
	 */
	public static SmAuthorizedPersonnelCountVO getSmAuthorizedPersonnelCountVO(
			SmAppl appl) {
		SmAuthorizedPersonnelCountVO vo = new SmAuthorizedPersonnelCountVO();
		PersonnelReqDTO reqDTO = new PersonnelReqDTO();
		reqDTO.setApplNo(appl.getApplNo());
		List<SmPersonnel> personnels = SmServiceLookup.getPersonnelSO()
				.retrievePersonnelsByApplNo(reqDTO).getSmPersonnelList();

		if (!EntityHelper.isCollectionEmpty(personnels)) {
			int maxAuthorizedSupplierUser = SmRuleHelper.getInstance()
					.getMaxAdditionalMofAuthorizedUser();

			int countAp = 0;
			int countApSupplierUser = 0;
			for (SmPersonnel per : personnels) {
				if (per.getAuthorizedFlg() == DbValueConst.RECORD_STATUS_ACTIVE
						&& per.getRecordStatusFlg() != DbValueConst.RECORD_STATUS_DELETED) {
					countAp++;
					if (!StringUtils.isBlank(per.getEpRole())
							&& (per.getEpRole().equals(
									RoleCode.MOF_SUPPLIER_USER.value())
									|| per.getEpRole()
											.equals(RoleCode.MOF_SUPPLIER_ADMIN
													.value())
									|| per.getEpRole().equals(
											RoleCode.SUPPLIER_TEMP.value()) || per
									.getEpRole().equals(
											RoleCode.BASIC_SUPPLIER_ADMIN
													.value()))) {
						countApSupplierUser++;
					}
				}
			}
			if (countAp >= maxAuthorizedSupplierUser) {
				vo.setReachAuthorizedPersonnelLimit(true);
			}
			if (countApSupplierUser >= maxAuthorizedSupplierUser) {
				vo.setReachSupplierUserAuthorizedLimit(true);
			}
		}
		return vo;
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
		int count = 0;
		int maxCatSize = SmRuleHelper.getInstance().getMaxCategoryAllowed();
		List<String> applTypes = Arrays.asList(
				ParameterConst.NEW_SM_APPLN_FOR_MOF,
				ParameterConst.NEW_SM_APPLN_FOR_APPLCATCODES,
				ParameterConst.NEW_SM_APPLN_FOR_RENEW);

		if (applTypes.contains(managedBean.getSmAppl().getApplType())) {
			for (SmSupplierCategory cat : managedBean.getSmAppl()
					.getSmSupplierCategories()) {
				if (cat.getRecordStatusFlg().compareTo(
						SupplierManagementConst.PENDING_ACT_STATUS) == 0
						|| cat.getRecordStatusFlg()
								.compareTo(
										SupplierManagementConst.PENDING_RE_APPROVAL_STATUS) == 0) {
					addedNewCode = true;
					count++;
				}
			}
		} else {
			addedNewCode = true;
		}

		if (count > maxCatSize) {
			throw new EpException(MessageConst.ERROR_722517,
					new String[] { maxCatSize + "" });
		}

		return addedNewCode;
	}

	/**
	 * Validate max new category added.
	 * 
	 * @param managedBean
	 *            - SmApplicationManagedBean object
	 */
	private static void validateMaxNewCategoryAdded(SmApplicationManagedBean managedBean) {
		int count = 0;
		int maxCatSize = SmRuleHelper.getInstance().getMaxCategoryAllowed();
		List<String> applTypes = Arrays.asList(ParameterConst.NEW_SM_APPLN_FOR_MOF,
				ParameterConst.NEW_SM_APPLN_FOR_APPLCATCODES, ParameterConst.NEW_SM_APPLN_FOR_RENEW);

		if (applTypes.contains(managedBean.getSmAppl().getApplType())) {
			for (SmSupplierCategory cat : managedBean.getSmAppl().getSmSupplierCategories()) {
				if (cat.getRecordStatusFlg().compareTo(SupplierManagementConst.PENDING_ACT_STATUS) == 0
						|| cat.getRecordStatusFlg().compareTo(SupplierManagementConst.PENDING_RE_APPROVAL_STATUS) == 0) {
					count++;
				}
			}
		}

		if (count > maxCatSize) {
			throw new EpException(MessageConst.ERROR_722517, new String[] { maxCatSize + "" });
		}
	}
	// 25996 start
	public static List<String> getCatCodesNotVerifiedByPo(SmApplicationManagedBean managedBean) {

		List<String> applTypes = Arrays.asList(ParameterConst.NEW_SM_APPLN_FOR_MOF,
				ParameterConst.NEW_SM_APPLN_FOR_APPLCATCODES, ParameterConst.NEW_SM_APPLN_FOR_RENEW);
		List<String> invalidCatCodes = new ArrayList<>();

		if (applTypes.contains(managedBean.getSmAppl().getApplType())) {
			for (SmSupplierCategory cat : managedBean.getSmAppl().getSmSupplierCategories()) {
				if (cat.getPoDecFlg() == null) {
					//Enhancement #11765
					invalidCatCodes.add(cat.getPmCategoryL1().getCategoryL1Code()+cat.getPmCategoryL2().getCategoryL2Code()+cat.getPmCategoryL3().getCategoryL3Code());
				}
			}
		}

		return invalidCatCodes;
	}
	// 25996 end
	
	//#26238 (39072)
	/**
	 * Retrieve unverified category codes by PO or Approver
	 * @param catList
	 * @param applType
	 * @return
	 */
	public static List<String> getUnverifiedCatCodes(List<SmSupplierCategory> catList, String applType) {

		List<String> applTypes = Arrays.asList(ParameterConst.NEW_SM_APPLN_FOR_MOF,
				ParameterConst.NEW_SM_APPLN_FOR_APPLCATCODES, ParameterConst.NEW_SM_APPLN_FOR_RENEW);
		List<Integer> validStatus = Arrays.asList(DbValueConst.RECORD_STATUS_ACTIVE, 
				DbValueConst.RECORD_STATUS_DELETED, DbValueConst.RECORD_STATUS_SUSPENDED);
		List<String> invalidCatCodes = new ArrayList<>();

		if (applTypes.contains(applType)) {
			//Enhancement #11765
			sortSupplierCategory(catList);
			for (SmSupplierCategory cat : catList) {
				// //Bug #25676 : SM Showstopper - No Categories Appear in Processing Officer Screen{
				//#30766 & #34209 - START
				if (!validStatus.contains(cat.getRecordStatusFlg()) && cat.getPoDecFlg() == null) {
				//#30766 & #34209 - END
					//Enhancement #11765
					invalidCatCodes.add(cat.getPmCategoryL1().getCategoryL1Code()
							+cat.getPmCategoryL2().getCategoryL2Code()+cat.getPmCategoryL3().getCategoryL3Code());
				}
			}
		}

		return invalidCatCodes;
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
					&& (EntityHelper.isCollectionEmpty(doc.getSmApplDocFiles())
							|| doc.getSmApplDocFiles().get(0).getFileName()
									.equalsIgnoreCase(" ") || doc
							.getSmApplDocFiles().get(0).getFilePath()
							.equalsIgnoreCase(" ")) //16052019 - SSM INTEGRATION CR - #27003
							&& !sb.toString().contains(doc.getPmDocument().getDocName())) {
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
	
	// #34392 - Start
	/**
	 * Validate supporting documents.For online application check whether
	 * supplier uploaded all the files or not
	 * 
	 * @param suppDocs
	 *            - List of SmApplSupportDoc
	 * @return true, if successful
	 */
	public static boolean validateSupportingDocsEnquiry(List<SmApplSupportDoc> suppDocs) {
		boolean validSupportDocs = true;
		int i = 0;
		StringBuilder sb = new StringBuilder();
		sb.append("<br/>");
		for (SmApplSupportDoc doc : suppDocs) {
			if (doc.getPmDocument() != null
					&& doc.getPmDocument().isMandatory()
					&& !doc.getPmDocument().isHardCopyReq()
					&& (EntityHelper.isCollectionEmpty(doc.getNewAddedFiles())
							|| doc.getNewAddedFiles().get(0).getFileName().equalsIgnoreCase(" ") || doc
							.getSmApplDocFiles().get(0).getFilePath().equalsIgnoreCase(" ")) // 16052019
																								// -
																								// SSM
																								// INTEGRATION
																								// CR
																								// -
																								// #27003
					&& !sb.toString().contains(doc.getPmDocument().getDocName())) {
				sb.append(++i + ".)");
				sb.append(doc.getPmDocument().getDocName());
				// sb.append(",  ");
				sb.append("<br/>");
				validSupportDocs = false;
			}
		}
		if (!validSupportDocs) {
			throw new EpException(MessageConst.ERROR_722686, new String[] { sb.toString() });
		} else {
			for (SmApplSupportDoc doc : suppDocs) {
				if (doc.getPmDocument() != null && doc.getPmDocument().isHardCopyReq()) {
					doc.getSmApplDocFiles().clear();
				}
			}
		}
		return validSupportDocs;
	}
	// #34392 - End

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
				throw new EpException(MessageConst.ERROR_721017);
			}
			UserReqDTO userReqDTO = new UserReqDTO();
			userReqDTO.setLoginId(pmUser.getLoginId());
			//log.info("************Supplier PmUser Login Id:"
					//+ pmUser.getLoginId());

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
			} 

			return smSupp;
		} catch (Exception e) {
			e.printStackTrace();
			throw new EpException(MessageUtils.getMessage(
					MessageConst.ERROR_721017,
					MessageUtils.BUNDLE_COMMON_MESSAGES,
					new FacesScopes().getUserLocale())
					+ " (" + e.getMessage() + ")");
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
								SupplierManagementConst.SM_SUPPLIER_BUSINESS_TYPE_ROB_PERSEORANGAN_SARAWAK)
				|| sup.getBusinessType()
						.equalsIgnoreCase(
								SupplierManagementConst.SM_SUPPLIER_BUSINESS_TYPE_PROFESSIONAL_BODY)) {
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
		} 
		//25792 - start
		else if(sup.getBusinessType().equalsIgnoreCase(SupplierManagementConst.SM_SUPPLIER_BUSINESS_TYPE_INDIVIDUAL)) {
			return SupplierManagementConst.SM_SUPPLIER_BUSINESS_TYPE_INDIVIDUAL;
		}
		//25792 - end
		else {
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
			String businessTypeVal, List<SmPersonnel> shrHldrs,SmSupplier smSupplier) {//change by fahmi, log : 23858 13/03/2019 : add smSupplier
		//Bug #30829 START
		if (personnel.isEquityOwner()) { // start - end = Bug #28313
			return true;
		//Bug #30829 END
		} else if(smSupplier != null && smSupplier.getBusinessType().equalsIgnoreCase(SupplierManagementConst.SM_SUPPLIER_BUSINESS_TYPE_PROFESSIONAL_BODY)){//change by fahmi, log : 23858 13/03/2019
			return true;
		}else if (businessTypeVal
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
							"/xhtml/sm/renewMofApplication/smRenewRegSideMenu_BasePage.xhtml");
			break;
		case "B":
			fc.getApplication()
					.getNavigationHandler()
					.handleNavigation(fc, null,
							"/xhtml/sm/renewMofApplication/smRenewRegSideMenu_BasePage.xhtml");
			break;
		case "U":
			fc.getApplication()
					.getNavigationHandler()
					.handleNavigation(fc, null,
							"/xhtml/sm/renewMofApplication/smRenewRegSideMenu_BasePage.xhtml");
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
			// if (stfCnt < 1) {
			// validCat = false;
			// throw new EpException(MessageConst.ERROR_722512,
			// new String[] { "staff" });
			// }
			// if (ownrCnt < 1) {
			// log.debug("****** No OWNER ********** ");
			// validCat = false;
			// throw new EpException(MessageConst.ERROR_722512,
			// new String[] { "equity owner" });
			// }
			// if (businessTypeVal !=
			// SupplierManagementConst.SM_SUPPLIER_BUSINESS_TYPE_ROB_INDIVIDUAL_DEFAULT
			// && businessTypeVal !=
			// SupplierManagementConst.SM_SUPPLIER_BUSINESS_TYPE_ROB_PARTNER_DEFAULT
			// && dirCnt < 1) {
			// validCat = false;
			// throw new EpException(MessageConst.ERROR_722512,
			// new String[] { "director" });
			// }

		}
		if (validCat) {
			if (category.getPmCategoryL3() != null
					&& category.getPmCategoryL3().getCategoryL3Id() != 99) {
				SmSupplierCategory cat = (SmSupplierCategory) EntityHelper
						.getEntityBySinglePropertyFromList(category
								.getPmCategoryL3().getCategoryL3Id(), appl
								.getSmSupplierCategories(), "pmCategoryL3");
				if (cat != null
						&& cat.getRecordStatusFlg() != DbValueConst.RECORD_STATUS_DELETED) {
					validCat = false;
					throw new EpException(MessageConst.ERROR_722525);
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
			String supplierType, String businessType,
			boolean isReviewSiteVisit, boolean showTravelPanel,
			boolean showMachineryPanel, boolean showFactoryPanel,
			boolean showDockyardPanel, boolean showStorePanel,
			boolean showWorkShopPanel) {
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
		} else {
			if (!businessType
					.equalsIgnoreCase(SupplierManagementConst.SM_SUPPLIER_BUSINESS_TYPE_ROC_DEFAULT)
					&& !businessType
							.equalsIgnoreCase(SupplierManagementConst.SM_SUPPLIER_BUSINESS_TYPE_LLP_DEFAULT)) {
				i = i - 1;
			}
		}

		if (isReviewSiteVisit) {
			i = i + 1;
		}
		if (applCatType.equals("S")) {
			if (!showTravelPanel) {
				i = i - 1;
			}
			if (!showMachineryPanel) {
				i = i - 1;
			}
			if (!showFactoryPanel) {
				i = i - 1;
			}
		}
		return i;
	}
	
	//#27356
	public static void validateCoordinate(SmApplicationManagedBean managedBean){
		SmCompanyBasic companyBasic = managedBean.getSmAppl().getSmCompanyBasic();
		List<SmFacility> facility =managedBean.getSmAppl().getSmFacilities();
		//START 27748 -  SM Disciplinary Action - 26238
		if(managedBean.getEditVO()!=null && managedBean.getEditVO().isDisciplinary()){
			return;
		}
		//END 27748 -  SM Disciplinary Action - 26238 
		if (companyBasic != null) {
			if (StringUtils.isBlank(companyBasic.getSmCompanyAddresses().get(0)
					.getSmAddress().getMapLatitude())
					|| StringUtils.isBlank(companyBasic.getSmCompanyAddresses()
							.get(0).getSmAddress().getMapLongitude())) {
				throw new EpException(SmCommonUtil.getMessage("lbl.sm.coordinate.required.msg",
						//#27853
						new String[] { SmCommonUtil.getMessage("lbl.sm.coordinate.company.info", null) }));
			}
		}
		if (facility != null) {
			for (SmFacility fac : facility) {
				// #32237 --START--
				if ((fac.getSmAddress().getMapLatitude() != null || fac.getSmAddress().getMapLongitude() != null)
				 //#29718 --START--
					&& ((!fac.getOwnershipType().equals("O") && (fac.getFacilityType().equals("H") || fac.getFacilityType().equals("B")) && managedBean.getCurrentPageType().equalsIgnoreCase("BASICINFO")) //Basic Info
						|| ((fac.getFacilityType().equals("F") || fac.getFacilityType().equals("W") || fac.getFacilityType().equals("D") || fac.getFacilityType().equals("S")) && managedBean.getCurrentPageType().equalsIgnoreCase("FACTORYINFO")))) { //Factory Info
				//#29718, #32237 --END-- 
					if (StringUtils.isBlank(fac.getSmAddress().getMapLatitude())
							|| StringUtils.isBlank(fac.getSmAddress().getMapLongitude())) {
						throw new EpException(SmCommonUtil.getMessage("lbl.sm.coordinate.required.msg",
								//#27853
								new String[] { SmCommonUtil.getMessage("lbl.sm.coordinate.permise.type", null) }));
					}
				}
			}
		}
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

	// harris
	public static void validateSSt(SmApplicationManagedBean managedBean,
			int currentTab, SmRuleHelper ruleHelper) {
		SmSstSupplier sst = managedBean.getCompanyBasicSst();
		if (!isSstEmpty(sst)) {
			if (StringUtils.isNotBlank(sst.getSalestaxregno())
					&& sst.getSalestaxregno().length() != ruleHelper
							.getSstRegNumLength()) {
				setNavigationBackToCurrentPage(currentTab, managedBean);
				throw new EpException(MessageConst.ERROR_721027);
			} else if (StringUtils.isNotBlank(sst.getSalestaxregno())
					&& sst.getSalestaxregDate() == null) {
				setNavigationBackToCurrentPage(currentTab, managedBean);
				// GST Effective date is required
				throw new EpException(MessageConst.ERROR_721030);
			} else if (StringUtils.isBlank(sst.getSalestaxregno())
					&& sst.getSalestaxregDate() != null) {
				setNavigationBackToCurrentPage(currentTab, managedBean);
				// GST Registration no is required
				throw new EpException(MessageConst.ERROR_721029);
			} else if (StringUtils.isBlank(sst.getSalestaxregno())
					&& sst.getSalestaxregEndDate() != null) {
				setNavigationBackToCurrentPage(currentTab, managedBean);
				// GST Registration no and GST Effective Date is required
				throw new EpException(MessageConst.ERROR_721043);
			} else if (sst.getSalestaxregEndDate() != null
					&& sst.getSalestaxregDate().compareTo(
							sst.getSalestaxregEndDate()) > 0) {
				setNavigationBackToCurrentPage(currentTab, managedBean);
				throw new EpException(MessageConst.ERROR_721044);
			}

			if (StringUtils.isNotBlank(sst.getServtaxregno())
					&& sst.getServtaxregno().length() != ruleHelper
							.getSstRegNumLength()) {
				setNavigationBackToCurrentPage(currentTab, managedBean);
				throw new EpException(MessageConst.ERROR_721027);
			} else if (StringUtils.isNotBlank(sst.getServtaxregno())
					&& sst.getServtaxregDate() == null) {
				setNavigationBackToCurrentPage(currentTab, managedBean);
				// GST Effective date is required
				throw new EpException(MessageConst.ERROR_721030);
			} else if (StringUtils.isBlank(sst.getServtaxregno())
					&& sst.getServtaxregDate() != null) {
				setNavigationBackToCurrentPage(currentTab, managedBean);
				// GST Registration no is required
				throw new EpException(MessageConst.ERROR_721029);
			} else if (StringUtils.isBlank(sst.getServtaxregno())
					&& sst.getServtaxregEndDate() != null) {
				setNavigationBackToCurrentPage(currentTab, managedBean);
				// GST Registration no and GST Effective Date is required
				throw new EpException(MessageConst.ERROR_721043);
			} else if (sst.getServtaxregEndDate() != null
					&& sst.getServtaxregDate().compareTo(
							sst.getServtaxregEndDate()) > 0) {
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
					// #34303 - start
					if (appl.getSmFacilities().get(i).isHeadquarters()) {
						appl.getSmFacilities().set(i, managedBean.getBasicCompanyFacility());
						break;
					}
					// #34303 - end
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
				sb.append(++noSignatoryCount + ") ");
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
				if (!isBranchAssignToBank(bean.getSmAppl(), brAppl) 
						&& brAppl.getRecordStatusFlg() != DbValueConst.RECORD_STATUS_DELETED) {	//#34135
					validSave = false;
					throw new EpException(MessageConst.ERROR_722622,
							new String[] { brAppl.getBranchName() });
				}
			}
		}
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
	public static void validateCompanyInformation(
			SmApplicationManagedBean bean, int currentTab, boolean validSave) {
		if (!SmCommonUtil.isCollectionEmpty(bean.getSmAppl()
				.getSmSupplierBranches())) {
			for (SmSupplierBranch brAppl : bean.getSmAppl()
					.getSmSupplierBranches()) {
				if (!isBranchAssignToBank(bean.getSmAppl(), brAppl)
						&& brAppl.getRecordStatusFlg() != DbValueConst.RECORD_STATUS_DELETED) { //#34135
					validSave = false;
					throw new EpException(MessageConst.ERROR_722622,
							new String[] { brAppl.getBranchName() });
				}
			}
		}
		validatePremiseInformation(bean.getSmAppl().getSmFacilities());
	}

	public static void validatePremiseInformation(List<SmFacility> facilities) {
		if (EntityHelper.isCollectionEmpty(facilities)) {
			throw new EpException(MessageConst.ERROR_722673);
		} else {
			boolean isPremiseExist = false;
			for (SmFacility fac : facilities) {
				if (fac.getFacilityType().equals("B")
						|| fac.getFacilityType().equals("H")) {
					isPremiseExist = true;
					break;
				}
			}
			if (!isPremiseExist) {
				throw new EpException(MessageConst.ERROR_722673);
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
			//#37605 START
			Map<Long, SmSupplierCategory> newCat = buildCategoryMap(managedBean.getActiveCategoryList());

		    for (SmSupplierCategory cat : appl.getSmSupplierCategories()) {
		        updateCategoryDescription(cat, newCat);
		        
		        if (cat.isSpecialCategory()) {
		            validateAndCleanCategory(cat);
		        }
		    }
			//#37605 END
			String errMsg = getCategoryOtherDescErrMsg(appl.getSmSupplierCategories());
			if (!StringUtils.isBlank(errMsg)) {
				setNavigationBackToCurrentPage(currentTab, managedBean);
				throw new EpException(MessageConst.ERROR_722514,
						new String[] { errMsg });
			}
		}
		return validSave;
	}
	
	public static String getCategoryOtherDescErrMsg(List<SmSupplierCategory> categoryList){
		StringBuilder sb = new StringBuilder();
		int i = 0;
		for (SmSupplierCategory category : categoryList) {
			if (category.getPmCategoryL3().getOtherDescReq() != null
					&& category.getPmCategoryL3().getOtherDescReq()
							.equals(new Integer(1))
					&& category.getRecordStatusFlg() != DbValueConst.RECORD_STATUS_ACTIVE
					&& category.getRecordStatusFlg() != DbValueConst.RECORD_STATUS_DELETED) {
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
				}
			}
		}
		return sb.length()>0?sb.toString():StringUtils.EMPTY;
	}

	public static void validateOverlapSuppDoc(boolean validSave,
			SmApplicationManagedBean managedBean, int currentTab) {
		if (!EntityHelper.isCollectionEmpty(managedBean.getSmAppl()
				.getSmCategoryOverlapDocs())) {
			for (SmCategoryOverlapDoc doc : managedBean.getSmAppl()
					.getSmCategoryOverlapDocs()) {
				if (doc.getPmDocument() != null
						&& StringUtils.isBlank(doc.getFileName())) {
					setNavigationBackToCurrentPage(currentTab, managedBean);
					throw new EpException(MessageConst.ERROR_722645,
							new String[] { doc.getPmDocument().getDocName() });
				}
			}
		}
	}

	//#30508-start
	public static void validateSpecialAndNormalCategory(boolean validSave, SmApplicationManagedBean managedBean,
			int currentTab) {
		boolean isSpecial = false;
		if(managedBean.isSupplierUser()){
			for (SmSupplierCategory category : managedBean.getSmAppl().getSmSupplierCategories()) {
				if (category.isSpecialCategory() && managedBean.getSmAppl().getApplCategoryType().equals("N")
						&& category.getRecordStatusFlg().equals(DbValueConst.RECORD_STATUS_PENDING_ACTIVATION)) {
					isSpecial = true;
				}
			}
			if (isSpecial) {
				throw new EpException(MessageConst.ERROR_722691);
			}	
		}
	}//#30508-end
	
	//#32236-start
	public static void validateSpecialAndNormalCategoryCapital(SmApplicationManagedBean managedBean) {
		StringBuilder bf = new StringBuilder();
		int index = 1;
			
		for (SmSupplierCategory category : managedBean.getSmAppl().getSmSupplierCategories()) {
			// paid up capital must less than paid-up capital of category
			// code
			if (category.getRecordStatusFlg().equals(DbValueConst.RECORD_STATUS_PENDING_ACTIVATION)
					|| category.getRecordStatusFlg().equals(DbValueConst.RECORD_STATUS_PENDING_RE_APPROVAL)
					|| category.getRecordStatusFlg().equals(DbValueConst.RECORD_STATUS_ACTIVE)) {
				if (managedBean.getSmAppl().getSmCompanyBasic().getPaidUpCapital()
						.compareTo(category.getPmCategoryL3().getPaidUpCapital()) < 0) {
					if (index == 1) {
						bf.append("<br>" + index++ + ". " + category.getPmCategoryL1().getCategoryL1Code()
								+ category.getPmCategoryL2().getCategoryL2Code()
								+ category.getPmCategoryL3().getCategoryL3Code() + " - RM"
								+ category.getPmCategoryL3().getPaidUpCapital() + "<br>");
					} else if (index > 1) {
						bf.append(index++ + ". " + category.getPmCategoryL1().getCategoryL1Code()
								+ category.getPmCategoryL2().getCategoryL2Code()
								+ category.getPmCategoryL3().getCategoryL3Code() + " - RM"
								+ category.getPmCategoryL3().getPaidUpCapital() + "<br>");
					}
				}
			}
		}

		if (managedBean.isSupplierUser()) {
			if (index > 1) {
				throw new EpException(MessageConst.ERROR_722693, new String[] { bf.toString() });
			}
		} else if (managedBean.isApproverUser() || managedBean.isPoUser()) {
			if (index > 1) {
				throw new EpException(MessageConst.ERROR_722695, new String[] { bf.toString() });
			}
		}
	}
		
	/**
	 * Get whether supplier already is business office and had approved special
	 * category
	 * @param managedBean 
	 */
	public static void setBusinessAndHadApprovedSpecialCatcode(SmApplicationManagedBean managedBean) {
		managedBean.setAlreadyBusinessSpecialCatcode(false);
		StringBuilder bf = new StringBuilder();
		int index = 1;

		SmFacility hq = getHq(managedBean);
		if (hq != null && hq.getFacilityType().equals("H")) {
			for (SmSupplierCategory cat : managedBean.getSmAppl().getSmSupplierCategories()) {
				if (cat.getRecordStatusFlg() == DbValueConst.RECORD_STATUS_ACTIVE
						&& cat.getPmCategoryL3().getHomeOperated() != DbValueConst.RECORD_STATUS_ACTIVE
						// Bug #27188-start
						|| cat.getRecordStatusFlg() == DbValueConst.RECORD_STATUS_PENDING_ACTIVATION
						&& cat.getPmCategoryL3().getHomeOperated() != DbValueConst.RECORD_STATUS_ACTIVE
						|| cat.getRecordStatusFlg() == DbValueConst.RECORD_STATUS_PENDING_RE_APPROVAL
						&& cat.getPmCategoryL3().getHomeOperated() != DbValueConst.RECORD_STATUS_ACTIVE) {
					// Bug #27188-end
					managedBean.setAlreadyBusinessSpecialCatcode(true);
					if (index == 1) {
						bf.append("<br> " + index + "." + " " + cat.getPmCategoryL1().getCategoryL1Code()
								+ cat.getPmCategoryL2().getCategoryL2Code() + cat.getPmCategoryL3().getCategoryL3Code()
								+ "," + "<br />");
						index++;
					} else if (index > 1) {
						bf.append(index + "." + " " + cat.getPmCategoryL1().getCategoryL1Code()
								+ cat.getPmCategoryL2().getCategoryL2Code() + cat.getPmCategoryL3().getCategoryL3Code()
								+ "," + "<br />");
						index++;
					}
				}
				// }
			}
		}

		if (index > 1) {
			if (managedBean.isApproverUser() || managedBean.isPoUser()) {
				throw new EpException(MessageConst.ERROR_722696, new String[] { bf.toString() });
			} else if (managedBean.isSupplierUser()) {
				throw new EpException(MessageConst.ERROR_722694, new String[] { bf.toString() });
			}
		}
	}
		
	/**
	 * Get HQ
	 * @param managedBean 
	 * 
	 * @return SmFacility
	 */
	private static SmFacility getHq(SmApplicationManagedBean managedBean) {
		SmFacility hq = null;
		if (!SmCommonUtil.isCollectionEmpty(managedBean.getSmAppl()
				.getSmFacilities())) {
			for (SmFacility fac : managedBean.getSmAppl()
					.getSmFacilities()) {
				if (StringUtils.isNotBlank(fac.getFacilityType())
						&& (fac.getFacilityType().equals("H") || fac
								.getFacilityType().equals("B")) && (fac.isHeadquarters())) {
					hq = fac;
					break;
				}
			}
		}
		return hq;
	}
	//#32236-end
	
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
		validateCategoryOtherDesc(validSave, managedBean, currentTab);
		validateOverlapSuppDoc(validSave, managedBean, currentTab);
		validateSpecialAndNormalCategory(validSave, managedBean, currentTab);//#30508
		//#32236 - start
		if(managedBean.isSupplierUser()){
			if(currentTab == 5    
					&& managedBean.getOutputPage() != null //#31018
					&& !managedBean.getOutputPage().equalsIgnoreCase(SupplierManagementConst.SM_APPL_PAGE_TYPE_EQUITY)){
				validateSpecialAndNormalCategoryCapital(managedBean);	
			}	
		}
		//#32236 - end

		if (!managedBean.isSiteVisitAfterApproval()) {
			//#11765
			//Need to allow user to move to other categories even if didn't add codes.
			/*if (!validSave) {
				setNavigationBackToCurrentPage(currentTab, managedBean);
				throw new EpException(MessageConst.ERROR_722570);
			}*/
			validateMaxNewCategoryAdded(managedBean);
		}

		//31043 start
		if (!managedBean.isSupplierUser()) {
			if (!SmCommonUtil.isCollectionEmpty(managedBean
					.getApprovedCategoryList())) {
				for (SmSupplierCategory cat : managedBean
						.getApprovedCategoryList()) {
					if (managedBean.getSmAppl().getApplType()
							.equals(ParameterConst.NEW_SM_APPLN_FOR_RENEW)) {
						if (managedBean.isPoUser()
								&& (cat.getRecordStatusFlg() == SupplierManagementConst.APPROVED_RECORD_STATUS
										|| cat.getRecordStatusFlg() == SupplierManagementConst.PENDING_RE_APPROVAL_STATUS || cat
										.getRecordStatusFlg() == SupplierManagementConst.PENDING_ACT_STATUS)) {
							
							//31043 start
							if(StringUtils.isNotEmpty(cat.getPoRemarks())){
								for(int i = 0; i < cat.getPoRemarks().length(); i++) {
									if (Character.isWhitespace(cat.getPoRemarks().charAt(i))) {
										cat.setPoRemarks(cat.getPoRemarks().trim());
									}
								}
							}
							//#35086 - start
							//#36576
							/*if(cat.getPoDecFlg() == null){
								cat.setPoDecFlg(0);
							}*/
							//#35086 - end
							//#31043 end
							if (cat.getPoDecFlg() != null && StringUtils.isBlank(cat.getPoRemarks())
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
												+ cat.getPmCategoryL1().getCategoryL1Code()
												+ cat.getPmCategoryL2().getCategoryL2Code()
												+ cat.getPmCategoryL3().getCategoryL3Code() }));
							}
						} else if (managedBean.isApproverUser()
								&& (cat.getRecordStatusFlg() == SupplierManagementConst.APPROVED_RECORD_STATUS
										|| cat.getRecordStatusFlg() == SupplierManagementConst.PENDING_RE_APPROVAL_STATUS || cat
										.getRecordStatusFlg() == SupplierManagementConst.PENDING_ACT_STATUS)) {
							//31043 start
							if(StringUtils.isNotEmpty(cat.getApproverRemarks())){
								for(int i = 0; i < cat.getApproverRemarks().length(); i++) {
									if (Character.isWhitespace(cat.getApproverRemarks().charAt(i))) {
										cat.setApproverRemarks(cat.getApproverRemarks().trim());
									}
								}
							}//#31043 end
							//#36576
							/*if (cat.getApprvrDecFlg() == null) {
								validSave = false;
								setNavigationBackToCurrentPage(currentTab,
										managedBean);
								throw new EpException(SmCommonUtil.getMessage(
										"lbl.sm.required.msg",
										new String[] { SmCommonUtil.getMessage(
												"lbl.sm.catCodes.appAct",null)
												+ " "
												+ SmCommonUtil.getMessage("lbl.sm.catCodes.forCatCode",null)
												+ " "
												+ cat.getPmCategoryL1().getCategoryL1Code()
												+ cat.getPmCategoryL2().getCategoryL2Code()
												+ cat.getPmCategoryL3().getCategoryL3Code() }));
							}*/
							if (cat.getApprvrDecFlg() != null && StringUtils.isBlank(cat.getApproverRemarks())
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
												+ cat.getPmCategoryL1().getCategoryL1Code()
												+ cat.getPmCategoryL2().getCategoryL2Code()
												+ cat.getPmCategoryL3().getCategoryL3Code() }));
							}
						}
					} else {
						if (managedBean.isPoUser()
								&& cat.getRecordStatusFlg() == SupplierManagementConst.PENDING_ACT_STATUS) {
							
							//31043 start
							if(StringUtils.isNotEmpty(cat.getPoRemarks())){
								for(int i = 0; i < cat.getPoRemarks().length(); i++) {
									if (Character.isWhitespace(cat.getPoRemarks().charAt(i))) {
										cat.setPoRemarks(cat.getPoRemarks().trim());
									}
								}
							}//#31043 end
							//#36576
							/*if (StringUtils.isBlank(cat.getPoRemarks())
									&& cat.getPoDecFlg() == null) {
								validSave = false;
								setNavigationBackToCurrentPage(currentTab,
										managedBean);
								throw new EpException(SmCommonUtil.getMessage(
										"lbl.sm.required.msg",
										new String[] { SmCommonUtil.getMessage(
												"lbl.sm.catCodes.poAct",
												null)
												+ " "
												+ SmCommonUtil.getMessage("lbl.sm.catCodes.forCatCode",null)
												+ " "
												+ cat.getPmCategoryL1().getCategoryL1Code()
												+ cat.getPmCategoryL2().getCategoryL2Code()
												+ cat.getPmCategoryL3().getCategoryL3Code() }));
							}*/
							if (cat.getPoDecFlg() != null && StringUtils.isBlank(cat.getPoRemarks())
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
							//31043 start
							if(StringUtils.isNotEmpty(cat.getApproverRemarks())){
								for(int i = 0; i < cat.getApproverRemarks().length(); i++) {
									if (Character.isWhitespace(cat.getApproverRemarks().charAt(i))) {
										cat.setApproverRemarks(cat.getApproverRemarks().trim());
									}
								}
							}//#31043 end
							//#36576
							/*if (cat.getApprvrDecFlg() == null) {
								validSave = false;
								setNavigationBackToCurrentPage(currentTab,
										managedBean);
								throw new EpException(
										SmCommonUtil.getMessage(
												"lbl.sm.required.msg",
												new String[] { SmCommonUtil.getMessage(
														"lbl.sm.catCodes.appAct",
														null)
														+ " "
														+ SmCommonUtil.getMessage("lbl.sm.catCodes.forCatCode",null)
														+ " "
														+ cat.getPmCategoryL1().getCategoryL1Code()
														+ cat.getPmCategoryL2().getCategoryL2Code()
														+ cat.getPmCategoryL3().getCategoryL3Code() }));
							}*/
							if (cat.getApprvrDecFlg() != null && StringUtils.isBlank(cat.getApproverRemarks())
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
												+ cat.getPmCategoryL1().getCategoryL1Code()
												+ cat.getPmCategoryL2().getCategoryL2Code()
												+ cat.getPmCategoryL3().getCategoryL3Code() }));
							}
						}
					}
				}
			}
			//31043 end
			
			//31043 start
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
								//#11765
								/*validSave = false;
								setNavigationBackToCurrentPage(currentTab, managedBean);
								throw new EpException(SmCommonUtil.getMessage("lbl.sm.required.msg",
										new String[] { SmCommonUtil.getMessage("lbl.sm.catCodes.poAct", null) + " "
												+ SmCommonUtil.getMessage("lbl.sm.catCodes.forCatCode", null) + " "
												+ cat.getPmCategoryL1().getCategoryL1Code()
												+ cat.getPmCategoryL2().getCategoryL2Code()
												+ cat.getPmCategoryL3().getCategoryL3Code() }));
								*/
								break;
							}
							//31043 start
							if(StringUtils.isNotEmpty(cat.getPoRemarks())){
								for(int i = 0; i < cat.getPoRemarks().length(); i++) {
									if (Character.isWhitespace(cat.getPoRemarks().charAt(i))) {
										cat.setPoRemarks(cat.getPoRemarks().trim());
									}
								}
							}//#31043 end
							//#35086 - start
							/*//#36576
							if(cat.getPoDecFlg() == null){
								cat.setPoDecFlg(0);
							}*/
							if (cat.getPoDecFlg() != null && StringUtils.isBlank(cat.getPoRemarks())
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
												+ cat.getPmCategoryL1().getCategoryL1Code()
												+ cat.getPmCategoryL2().getCategoryL2Code()
												+ cat.getPmCategoryL3().getCategoryL3Code() }));
							}
						} else if (managedBean.isApproverUser()
								&& (cat.getRecordStatusFlg() == SupplierManagementConst.APPROVED_RECORD_STATUS
										|| cat.getRecordStatusFlg() == SupplierManagementConst.PENDING_RE_APPROVAL_STATUS || cat
										.getRecordStatusFlg() == SupplierManagementConst.PENDING_ACT_STATUS)) {
							//31043 start
							if(StringUtils.isNotEmpty(cat.getApproverRemarks())){
								for(int i = 0; i < cat.getApproverRemarks().length(); i++) {
									if (Character.isWhitespace(cat.getApproverRemarks().charAt(i))) {
										cat.setApproverRemarks(cat.getApproverRemarks().trim());
									}
								}
							}//#31043 end
							//#36576
							/*if (cat.getApprvrDecFlg() == null) {
								validSave = false;
								setNavigationBackToCurrentPage(currentTab,
										managedBean);
								throw new EpException(SmCommonUtil.getMessage(
										"lbl.sm.required.msg",
										new String[] { SmCommonUtil.getMessage(
												"lbl.sm.catCodes.appAct", null)
												+ " "
												+ SmCommonUtil.getMessage("lbl.sm.catCodes.forCatCode", null)
												+ " "
												+ cat.getPmCategoryL1().getCategoryL1Code()
												+ cat.getPmCategoryL2().getCategoryL2Code()
												+ cat.getPmCategoryL3().getCategoryL3Code() }));
							}*/
							if (cat.getApprvrDecFlg() != null && StringUtils.isBlank(cat.getApproverRemarks())
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
												+ cat.getPmCategoryL1().getCategoryL1Code()
												+ cat.getPmCategoryL2().getCategoryL2Code()
												+ cat.getPmCategoryL3().getCategoryL3Code() }));
							}
						}
					} else {
						if (managedBean.isPoUser()
								&& cat.getRecordStatusFlg() == SupplierManagementConst.PENDING_ACT_STATUS) {
							//31043 start
							if(StringUtils.isNotEmpty(cat.getPoRemarks())){
								for(int i = 0; i < cat.getPoRemarks().length(); i++) {
									if (Character.isWhitespace(cat.getPoRemarks().charAt(i))) {
										cat.setPoRemarks(cat.getPoRemarks().trim());
									}
								}
							}//#31043 end
							
							//if (cat.getPoDecFlg() == null) {
								//#11765
								/*validSave = false;
								setNavigationBackToCurrentPage(currentTab,
										managedBean);
								throw new EpException(SmCommonUtil.getMessage("lbl.sm.required.msg",
										new String[] { SmCommonUtil.getMessage("lbl.sm.catCodes.poAct", null) + " "
												+ SmCommonUtil.getMessage("lbl.sm.catCodes.forCatCode", null) + " "
												+ cat.getPmCategoryL1().getCategoryL1Code()
												+ cat.getPmCategoryL2().getCategoryL2Code()
												+ cat.getPmCategoryL3().getCategoryL3Code() }));*/
							//	break;
							//}
							//#30508-start
							//#36576
							/*if (StringUtils.isBlank(cat.getPoRemarks())
									&& cat.getPoDecFlg() == null) {
								validSave = false;
								setNavigationBackToCurrentPage(currentTab,
										managedBean);
								throw new EpException(SmCommonUtil.getMessage(
										"lbl.sm.required.msg",
										new String[] { SmCommonUtil.getMessage("lbl.sm.catCodes.poAct", null)
										+ " "
										+ SmCommonUtil.getMessage("lbl.sm.catCodes.forCatCode", null)
										+ " "
										+ cat.getPmCategoryL1().getCategoryL1Code()
										+ cat.getPmCategoryL2().getCategoryL2Code()
										+ cat.getPmCategoryL3().getCategoryL3Code() }));
							}*/
							if (cat.getPoDecFlg() != null && StringUtils.isBlank(cat.getPoRemarks())
									&& cat.getPoDecFlg() == 0) {
								validSave = false;
								setNavigationBackToCurrentPage(currentTab, managedBean);//#30508-end
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
							//31043 start
							if(StringUtils.isNotEmpty(cat.getApproverRemarks())){
								for(int i = 0; i < cat.getApproverRemarks().length(); i++) {
									if (Character.isWhitespace(cat.getApproverRemarks().charAt(i))) {
										cat.setApproverRemarks(cat.getApproverRemarks().trim());
									}
								}
							}//#31043 end
							//#36576
							/*if (cat.getApprvrDecFlg() == null) {
								validSave = false;
								setNavigationBackToCurrentPage(currentTab,
										managedBean);
								throw new EpException(
										SmCommonUtil.getMessage("lbl.sm.required.msg",
										new String[] { SmCommonUtil.getMessage("lbl.sm.catCodes.appAct", null)
										+ " "
										+ SmCommonUtil.getMessage("lbl.sm.catCodes.forCatCode", null)
										+ " "
										+ cat.getPmCategoryL1().getCategoryL1Code()
										+ cat.getPmCategoryL2().getCategoryL2Code()
										+ cat.getPmCategoryL3().getCategoryL3Code() }));
							}*/
							if (cat.getApprvrDecFlg() != null && StringUtils.isBlank(cat.getApproverRemarks())
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
												+ cat.getPmCategoryL1().getCategoryL1Code()
												+ cat.getPmCategoryL2().getCategoryL2Code()
												+ cat.getPmCategoryL3().getCategoryL3Code() }));
							}
						}
					}
				}
			}
			//#31043 end
		}
		return validSave;
	}

	public static boolean validatePersonnelAcademics(boolean validSave,
			SmApplicationManagedBean managedBean, int currentTab) {
		boolean validPersonnels = true;
		StringBuilder sb = new StringBuilder();
		int i = 0;
		managedBean.setPoApproverAcedemicErr(false);//#31130
		SmAppl appl = managedBean.getSmAppl();
		for (SmPersonnel per : appl.getSmPersonnels()) {
			//#31130 --START 040725
			if ((per.getPmUser() != null || per.getEpRole() != null) && (per.getSmStaffAcademics() == null
					|| per.getSmStaffAcademics().isEmpty())) {
				//#31130 --END 040725
				sb.append("<br/>");
				sb.append(++i + ". ");
				sb.append(per.getName());
				validPersonnels = false;
			}
		}
		//#31130 --START 040725
		if (!validPersonnels && managedBean.isSupplierUser()){//#31130
			validSave = false;
		}else if (!validPersonnels && (managedBean.isApproverUser()|| managedBean.isPoUser())){
			managedBean.setPoApproverAcedemicErr(true);
			managedBean.setMsgPersonnelErr(sb.toString());
		//#31130 --END 040725
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
		//16052019 - SSM INTEGRATION CR
		int i = 0;
		int j = 0; //#30444
		StringBuilder sb = new StringBuilder();
		sb.append("<br/>");
		StringBuilder sbd = new StringBuilder(); //#30444 --31072021--
		sbd.append("<br/>"); //#30444 --31072021--
		boolean validPersonnel = true;
		boolean validPersonnels = true; //#30444
		boolean displayError = false; //#30444 --31072021--
		boolean showerror = false;
		managedBean.setPoApproverPersonnelErr(false); //#31130 040725
		for (SmPersonnel per : appl.getSmPersonnels()) {
			//16052019 - SSM INTEGRATION CR - To check address complete or not
			/*if(managedBean.isSsmIntegrationActive() && managedBean.isCompanyValidWithSSM()){
				if(per.getSmPersonnelAddresses()==null || per.getSmPersonnelAddresses().size()<=0){
					log.info("ini tiada address");
				}
			}*/
			validPersonnel = true;
			validPersonnels = true; //#30444 --31072021--
			//#28693
			if ((per.isBumi() == null && per.getBumiFlg() == null && per.getBumiFlgTrans() == null)
					// #30718 - start
					|| (per.isMuslim() == null && per.getMuslimFlg() == null && per.getMuslimFlgTrans() == null 
							&& (per.getPmCountry() != null && per.getPmCountry().getCountryId() == 131L)) //#31130 -040725
					// #30718 - end
					//#31130 --START 040725
					|| per.getPmRace() == null || per.getTitle() == null
					|| per.getDesignation() == null 
					|| per.getPmCountry() == null || per.getIdentificationNo() == null
					|| per.getIdentificationTypeId() == null || per.getBirthDate() == null 
					|| (per.getWorkExperienceYear() == null && appl.getSupplierType().equalsIgnoreCase(
							SupplierManagementConst.STYPE_CONSULTANT)) 
					|| ((per.getMobileArea() == null || per.getMobileNo() == null|| per.getMobileTelcoId() == null)
							&& per.isAuthorized() == true)
					|| per.getSmPersonnelAddresses() == null || per.getSmPersonnelAddresses().size() <= 0) {
				validPersonnel = false;
			} //#31130 --END 040725
			
			//31130-start 040725
			for (SmPersonnelAddress addressPer : per.getSmPersonnelAddresses()){
				if (addressPer.getSmAddress() == null
						|| (addressPer.getSmAddress() != null && addressPer.getSmAddress().getAddress1().isEmpty()
								|| addressPer.getSmAddress().getPostcode() == null
								|| addressPer.getSmAddress().getCountryId() == null
								|| addressPer.getSmAddress().getStateId() == null
								|| addressPer.getSmAddress().getDistrictId() == null 
								|| addressPer.getSmAddress().getCityId() == null) 
								|| addressPer.getSmPersonnel() == null
								|| addressPer.getSmAddress().getPostcode().isEmpty()
								|| addressPer.getSmAddress().getDistrictId()== 1
								|| addressPer.getSmAddress().getCityId() == 1 ) {
					validPersonnel = false;
				}
			}//31130-end 040725
			//#30444 --START--25082021--
			if ((per.isClerical() && per.isManagement() 
					&& !managedBean.getBusinessTypeVal().equalsIgnoreCase(
							SupplierManagementConst.SM_SUPPLIER_BUSINESS_TYPE_ROB_PARTNER_DEFAULT))
							|| (per.isClerical() && per.isManagement() && !per.isEquityOwner()
									&& managedBean.getBusinessTypeVal().equalsIgnoreCase(
											SupplierManagementConst.SM_SUPPLIER_BUSINESS_TYPE_ROB_PARTNER_DEFAULT))){
				validPersonnels = false;
			}
			//#30444 --END--25082021--
			//#31130 --START--040725--
			if((per.getPmUser() != null || per.getEpRole() != null)
					&&(!validPersonnel || (appl.getSupplierType().equalsIgnoreCase(
					SupplierManagementConst.STYPE_CONSULTANT) 
					&& ((per.getSmStaffAcademics() == null || per.getSmStaffAcademics().isEmpty())
					|| (per.isEquityOwner() && (per.getSmStaffExperiences() == null || per.getSmStaffExperiences().isEmpty()))
					|| (per.isEquityOwner() && (per.getSmAssociationBodies() == null || per.getSmAssociationBodies().isEmpty())))))){
				//#31130 --END--040725--
				sb.append(++i + ". ");
				sb.append(per.getName());
				// sb.append(",  ");
				sb.append("<br/>");
				showerror = true;
				//show alert message here
			}
			//END Changes SSM
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

				}
				if (per.isProf() && !StringUtils.isBlank(per.getEpfNo())) {
					profCount = profCount + 1;

				}
				if (per.isSemiProf() && !StringUtils.isBlank(per.getEpfNo())) {
					semiProfCount = semiProfCount + 1;
				}
			}
			//#30444 --START--31072021--
			if(!validPersonnels){
				sbd.append(++j + ". ");
				sbd.append(per.getName());
				sbd.append("<br/>");
				displayError = true;				
			}
			//#30444 --END--31072021--
		}
		//#30444 --START	
		if (displayError && managedBean.isSupplierUser()) {	 //#30444 --31072021--	
			if (currentTab != -1) {
				setNavigationBackToCurrentPage(currentTab, managedBean);
			}
			if (appl.getSupplierType().equalsIgnoreCase(
					SupplierManagementConst.STYPE_CONTRACTOR)){
				throw new EpException(MessageConst.ERROR_722687, new String[]
						{sbd.toString() });
			}
			if (appl.getSupplierType().equalsIgnoreCase(
					SupplierManagementConst.STYPE_CONSULTANT)){
			throw new EpException(MessageConst.ERROR_722688, new String[]
					{sbd.toString() });
			}
		}
		//#30444 --END
		
		// start #30076
		boolean validateSmBranch = true;
		StringBuilder sbSuppBranch = new StringBuilder();
		sbSuppBranch.append("<br/>");
		int x = 0;
		//32968 START
		String notAvail = "N/A";
		for (SmSupplierBranch suppBranch : appl.getSmSupplierBranches()) {
			if ((suppBranch.getBranchName().isEmpty()		//34135 START
					|| suppBranch.getSmAddress() == null
					|| (suppBranch.getSmAddress() != null && suppBranch.getSmAddress().getAddress1().isEmpty()
							|| suppBranch.getSmAddress().getPostcode() == null
							|| suppBranch.getSmAddress().getCountryId() == null
							|| suppBranch.getSmAddress().getStateId() == null
							|| suppBranch.getSmAddress().getDistrictId() == null || suppBranch.getSmAddress()
							.getCityId() == null) || suppBranch.getSmPersonnel() == null
							|| suppBranch.getSmAddress().getPmDistrict().getDistrictName().contains(notAvail)
							|| suppBranch.getSmAddress().getPmCity().getCityName().contains(notAvail)	//32968 END)
							|| (suppBranch.getSmAddress().getDivisionId() == null									
							&& suppBranch.getSmAddress().getPmState().getStateCode().equalsIgnoreCase("13")))		
							&& suppBranch.getSmAddress().getPmCountry().getCountryCode().equalsIgnoreCase("MY")) {  //34135 END
				validateSmBranch = false;
				sbSuppBranch.append(++x + ". ");
				sbSuppBranch.append(suppBranch.getBranchName());
				sbSuppBranch.append("<br/>");
			}
		}
		//32968 START
		if (!validateSmBranch) {
			validSave = false;
			if(!managedBean.isCheckBranch() && (managedBean.isPoUser() || managedBean.isApproverUser())){
				if (currentTab != -1) {
					setNavigationBackToCurrentPage(currentTab, managedBean);
				}
				managedBean.setCheckBranch(true);
				throw new EpException(SmCommonUtil.getMessage("lbl.sm.mandatory.branch.not.completed",
						new String[] { sbSuppBranch.toString() }));
			}else if(managedBean.isSupplierUser()){
				//StringBuilder errMsg = new StringBuilder();
				//errMsg.append("Please complete the mandatory fields marked (*) in the 'Personnel/Branch Info' section for the following Company Branch:</br> ");
				//errMsg.append(sbSuppBranch);
				if (currentTab != -1) {
					setNavigationBackToCurrentPage(currentTab, managedBean);
				}
				//throw new EpException(errMsg.toString());
				throw new EpException(SmCommonUtil.getMessage("lbl.sm.supplier.branch.not.completed",
						new String[] { sbSuppBranch.toString() }));
			}
		}
		//32968 END
		// end #30076

		//16052019 - SSM INTEGRATION CR #27219
		//31130 040725
		if (showerror && managedBean.isSupplierUser() && !managedBean.isDisAllowOwnerEdit() && !managedBean.isApproverUser() && !managedBean.isPoUser()) {
			if (currentTab != -1) {
				setNavigationBackToCurrentPage(currentTab, managedBean);
			}
			throw new EpException(MessageConst.ERROR_721082,
					new String[] { sb.toString() });
		}
		//#31130-start 040725
		if (showerror && (managedBean.isApproverUser() || managedBean.isPoUser())){
			managedBean.setPoApproverPersonnelErr(true);
			managedBean.setMsgPersonnelErr(sb.toString());
			}else{
				managedBean.setPoApproverPersonnelErr(false);
				}
		//#31130-end 040725
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
				if (currentTab != -1) {
					setNavigationBackToCurrentPage(currentTab, managedBean);
				}
				throw new EpException(errMsg.toString());
			}
		}
		if (appl.getSupplierType().equalsIgnoreCase(
				SupplierManagementConst.STYPE_CONTRACTOR)) {
			if (staffCount < 1) {
				validSave = false;
				if (currentTab != -1) {
					setNavigationBackToCurrentPage(currentTab, managedBean);
				}
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
			if (currentTab != -1) {
				setNavigationBackToCurrentPage(currentTab, managedBean);
			}
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

			String directorTotal = SmCommonUtil.getMessage(
					"lbl.sm.personnel.two.lowercaps", null);
			if (managedBean
					.getSmSupplier()
					.getBusinessType()
					.equals(SupplierManagementConst.SM_SUPPLIER_BUSINESS_TYPE_ROC_SDN_BHD)
					&& directorCount < 1) {
				directorTotal = SmCommonUtil.getMessage(
						"lbl.sm.personnel.one.lowercaps", null);
				validSave = false;
			} else if (managedBean
					.getSmSupplier()
					.getBusinessType()
					.equals(SupplierManagementConst.SM_SUPPLIER_BUSINESS_TYPE_ROC_BHD)
					&& directorCount < 2) {
				validSave = false;
			}

			if (!validSave) {
				if (currentTab != -1) {
					setNavigationBackToCurrentPage(currentTab, managedBean);
				}
				throw new EpException(MessageConst.ERROR_722513, new String[] {
						SmCommonUtil.getMessage(
								"lbl.sm.personnel.comprole.director", null),
						directorTotal,
						SmCommonUtil.getMessage(
								"lbl.sm.personnel.comprole.director", null) });
			}

		}
		if (mgmtCount < 1) {
			validSave = false;
			if (currentTab != -1) {
				setNavigationBackToCurrentPage(currentTab, managedBean);
			}
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
		managedBean.setPoApproverEquityErr(false);//#31461
		managedBean.setPoApproverChgBusiness(false); //#30691 - 26112021
		// change by fahmi, log : 23858 13/03/2019 
		if (managedBean.getBusinessTypeVal() != null
				&& (managedBean.getBusinessTypeVal()
						.equalsIgnoreCase(SupplierManagementConst.SM_SUPPLIER_BUSINESS_TYPE_ROB_INDIVIDUAL_DEFAULT)) && (managedBean.getSmSupplier() != null && !SupplierManagementConst.SM_SUPPLIER_BUSINESS_TYPE_PROFESSIONAL_BODY.equalsIgnoreCase(managedBean.getSmSupplier().getBusinessType()))) {
			if (EntityHelper.isCollectionEmpty(managedBean
					.getSmapplShareHolders())
					|| (managedBean.getSmapplShareHolders().size() != 1)) {

				validCount = false;
				
				//#31461-start
				if (managedBean.isSupplierUser()){
					if (currentTab != -1) {
						setNavigationBackToCurrentPage(currentTab, managedBean);
					}
					throw new EpException(MessageConst.ERROR_722507);
				} else {
					// #30691 - Start
					if(managedBean.isSiteVisitAfterApproval() && (managedBean.getSmSupplier().getSmAppl() != managedBean.getSmAppl())){
						managedBean.setPoApproverChgBusiness(true);
						managedBean.setMsgChgBusiness(MessageConst.ERROR_770270);
					} else { 
					// #30691 - End
						managedBean.setPoApproverEquityErr(true);
						managedBean.setMsgEquityErr(MessageConst.ERROR_722507);
					}
				}//#31461-end
			}
		} else  if (managedBean.getBusinessTypeVal() != null
				&& (managedBean
						.getBusinessTypeVal()
						.equalsIgnoreCase(
								SupplierManagementConst.SM_SUPPLIER_BUSINESS_TYPE_ROC_DEFAULT) || managedBean
						.getBusinessTypeVal()
						.equalsIgnoreCase(
								SupplierManagementConst.SM_SUPPLIER_BUSINESS_TYPE_LLP_DEFAULT))) {
			if (currentPageType
					.equals(SupplierManagementConst.SM_APPL_PAGE_TYPE_EQUITY)) {
				String messageKey = MessageConst.ERROR_722592;
				int minCount = 2;

				if (managedBean
						.getBusinessTypeVal()
						.equalsIgnoreCase(
								SupplierManagementConst.SM_SUPPLIER_BUSINESS_TYPE_ROC_DEFAULT)) {
					messageKey = MessageConst.ERROR_722657;
					minCount = 1;
				}
				int perShrCnt = 0;
				int compShrCnt = 0;
				if (managedBean.getSmapplShareHolders() != null) {
					perShrCnt = managedBean.getSmapplShareHolders().size();
				}
				if (managedBean.getSmAppl().getSmEquityCompanies() != null) {
					compShrCnt = managedBean.getSmAppl().getSmEquityCompanies()
							.size();
				}
				if (perShrCnt + compShrCnt < minCount) {
					validCount = false;
					//#31461-start
					if (managedBean.isSupplierUser()) {
						if (currentTab != -1) {
							setNavigationBackToCurrentPage(currentTab, managedBean);
						}
						throw new EpException(messageKey);
					} else {
						// #30691 - Start
						if(managedBean.isSiteVisitAfterApproval() && (managedBean.getSmSupplier().getSmAppl() != managedBean.getSmAppl())){
							managedBean.setPoApproverChgBusiness(true);
							managedBean.setMsgChgBusiness(MessageConst.ERROR_770270);
						} else { 
						// #30691 - End
							managedBean.setPoApproverEquityErr(true);
							managedBean.setMsgEquityErr(messageKey);
						}
					}//#31461-end
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
			//#31461-start
			if (managedBean.isSupplierUser()) {
				if (currentTab != -1) {
					setNavigationBackToCurrentPage(currentTab, managedBean);
				}
				throw new EpException(MessageConst.ERROR_722508);
			} else {
				// #30691 - Start
				if(managedBean.isSiteVisitAfterApproval() && (managedBean.getSmSupplier().getSmAppl() != managedBean.getSmAppl())){
					managedBean.setPoApproverChgBusiness(true);
					managedBean.setMsgChgBusiness(MessageConst.ERROR_770270);
				} else { 
				// #30691 - End
					managedBean.setPoApproverEquityErr(true);
					managedBean.setMsgEquityErr(MessageConst.ERROR_722508);
				}
			}//#31461-end
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
			//25929 - start
			BigDecimal companiesEquityTotal = new BigDecimal(0);
			companiesEquityTotal = managedBean.getSmAppl().getSmCompanyBasic().getGovtUnitrustEquity();
			companiesEquityTotal = companiesEquityTotal.add(managedBean.getSmAppl().getSmCompanyBasic().getGovtEquity());
			companiesEquityTotal = companiesEquityTotal.add(managedBean.getSmAppl().getSmCompanyBasic().getForeignCompanyEquity());
			companiesEquityTotal = companiesEquityTotal.add(managedBean.getSmAppl().getSmCompanyBasic().getLocalCompanyEquity());
			managedBean.getSmAppl().getSmCompanyBasic().setCompaniesEquityTotal(companiesEquityTotal);
			//25929 - end
			if (managedBean.getSmAppl().getSmCompanyBasic()
					.getCompaniesEquityTotal() != null
					&& managedBean.getSmAppl().getSmCompanyBasic()
							.getCompaniesEquityTotal()
							.compareTo(new BigDecimal(0)) > 0
					&& managedBean.getEquiCompCapTotal().compareTo(
							new BigDecimal(0)) == 0) {
				validAmt = false;
				if (currentTab != -1) {
					setNavigationBackToCurrentPage(currentTab, managedBean);
				}
				throw new EpException(MessageConst.ERROR_722583);
			} else if (managedBean.getSmAppl().getSmCompanyBasic()
					.getCompaniesEquityTotal() != null
					&& managedBean.getSmAppl().getSmCompanyBasic()
							.getCompaniesEquityTotal()
							.compareTo(managedBean.getEquiCompCapTotal()) != 0) {
				validAmt = false;
				if (currentTab != -1) {
					setNavigationBackToCurrentPage(currentTab, managedBean);
				}
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
			validSave = validateFacility(validSave, currentTab, bean,
					SupplierManagementConst.SM_FACILITY_TYPE_FACTORY,
					"lbl.sm.facility.type.factory",
					bean.getFactoryFacilitiesVOList());

		}
		if (bean.isShowDockyardPanel()) {
			validSave = validateFacility(validSave, currentTab, bean,
					SupplierManagementConst.SM_FACILITY_TYPE_DOCKYARD,
					"lbl.sm.facility.type.dockyard",
					bean.getDockYardFacilitiesVOList());
		}
		if (bean.isShowWorkShopPanel()) {
			validSave = validateFacility(validSave, currentTab, bean,
					SupplierManagementConst.SM_FACILITY_TYPE_WORKSHOP,
					"lbl.sm.facility.type.workshop",
					bean.getWorkshopFacilitiesVOList());
		}
		if (bean.isShowStorePanel()) {
			validSave = validateFacility(validSave, currentTab, bean,
					SupplierManagementConst.SM_FACILITY_TYPE_STORE,
					"lbl.sm.facility.type.store",
					bean.getStoreFacilitiesVOList());
		}
		return validSave;
	}

	private static boolean validateFacility(boolean valid, int currentTab,
			SmApplicationManagedBean bean, String facType, String facTypeLabel,
			List<PmCategoryL3> categoryList) {
		if (!bean.getSmAppl().getApplType()
				.equals(ParameterConst.NEW_SM_APPLN_FOR_RENEW)) {

			if (!EntityHelper.isCollectionEmpty(categoryList)) {
				for (PmCategoryL3 l3 : categoryList) {
					if (EntityHelper.isCollectionEmpty(l3.getFacilityList())) {
						valid = false;
						if (currentTab != -1) {
							setNavigationBackToCurrentPage(currentTab, bean);
						}
						throw new EpException(MessageConst.ERROR_722621,
								new String[] {
										SmCommonUtil.getMessage(facTypeLabel,
												null),
										l3.getFullCatcode() + " ("
												+ l3.getCategoryName() + ")" });
					}
				}
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
		//#34277 - start
		if(managedBean.getSmAppl().getSmCompanyBasic().getPaidUpCapital() == null){
			managedBean.getSmAppl().getSmCompanyBasic().setPaidUpCapital(new BigDecimal(0));
		}
		//#34277 - end
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
				if (currentTab != -1) {
					setNavigationBackToCurrentPage(currentTab, managedBean);
				}
				
				if (managedBean.getSmAppl().isApplValidWithSsm()) {
					if (managedBean.getSmSupplier().getBusinessType().equalsIgnoreCase(
							SupplierManagementConst.SM_SUPPLIER_BUSINESS_TYPE_ROC_SDN_BHD)
							|| managedBean.getSmSupplier().getBusinessType().equalsIgnoreCase(
									SupplierManagementConst.SM_SUPPLIER_BUSINESS_TYPE_ROC_BHD)) {
						throw new EpException(MessageConst.ERROR_722677, new String[] { s, sb.toString(), supType });
					} else {
						throw new EpException(MessageConst.ERROR_722555, new String[] { s, sb.toString(), supType });
					}
				}else{//#27260
					throw new EpException(MessageConst.ERROR_722555, new String[] { s, sb.toString(), supType });
				}
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
			BigDecimal bumiShrSum = new BigDecimal(0);
			BigDecimal foreignShrSum = new BigDecimal(0);
			for (SmPersonnel per : managedBean.getSmapplShareHolders()) {
				if (per.getShareAmt() == null
						|| per.getShareAmt().compareTo(new BigDecimal(0)) == 0) {
					sb.append("<br/>");
					sb.append(++i + ") ");
					sb.append(per.getName());
					validShrHldrsCap = false;
				} else {
					perShrSum = perShrSum.add(per.getShareAmt());
					if (per.getBumiFlg() != null && per.getBumiFlg() == DbValueConst.RECORD_STATUS_ACTIVE) {
						bumiShrSum = bumiShrSum.add(per.getShareAmt());
					}
					if (per.getPmCountry() != null
							&& !per.getPmCountry()
									.getCountryCode()
									.equals(SupplierManagementConst.MALAYSIA_CODE)) {
						foreignShrSum = foreignShrSum.add(per.getShareAmt());
					}
				}
			}
			if (!validShrHldrsCap) {
				validAmt = false;
				if (currentTab != -1) {
					setNavigationBackToCurrentPage(currentTab, managedBean);
				}
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
				if (currentTab != -1) {
					setNavigationBackToCurrentPage(currentTab, managedBean);
				}
				throw new EpException(MessageConst.ERROR_722554);
			}
			// check bumi share sum is exactly same as bumi equity
			else if (bumiShrSum.compareTo(managedBean.getSmAppl()
					.getSmCompanyBasic().getBumiEquity()) != 0) {
				validAmt = false;
				if (currentTab != -1) {
					setNavigationBackToCurrentPage(currentTab, managedBean);
				}
				throw new EpException(MessageConst.ERROR_722554);
			}
			// check foreign share sum is exactly same as foreign equity
			else if (foreignShrSum.compareTo(managedBean.getSmAppl()
					.getSmCompanyBasic().getForeignerEquity()) != 0) {
				validAmt = false;
				if (currentTab != -1) {
					setNavigationBackToCurrentPage(currentTab, managedBean);
				}
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
		//SSM INTEGRATION #27110
		boolean showError = true;
		if(appl.isApplValidWithSsm() && managedBean.getCurrentPageType()
				.equalsIgnoreCase(SupplierManagementConst.SM_APPL_PAGE_TYPE_PERSONNEL)
				){
			showError = false;
			
		}
		if (appl != null && appl.getSmCompanyBasic() != null
				&& appl.getSmCompanyBasic().getPaidUpCapital() != null
				&& appl.getSmCompanyBasic().getAuthorizedCapital() != null
				&& appl.getSmCompanyBasic().getTotalShare() != null) {
			//SSM INTEGRATION -  No Need To Check Authorized capital vs paid up capital cz new rule ssm, authorize capital not mandatory
			/*if ((managedBean
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
				if(showError){
					throw new EpException(MessageConst.ERROR_722523,
							new String[] { SmCommonUtil.getMessage(
									"lbl.sm.authorized.capital", null) });
				}
				
			}*/
			// 28466 - compare calculated total with paid up capital
			if ((managedBean.getBusinessTypeVal().equalsIgnoreCase(
					SupplierManagementConst.SM_SUPPLIER_BUSINESS_TYPE_ROS_DEFAULT) || managedBean.getBusinessTypeVal()
					.equalsIgnoreCase(SupplierManagementConst.SM_SUPPLIER_BUSINESS_TYPE_ROC_DEFAULT))
					&& appl.getSmCompanyBasic()
							.getPaidUpCapital()
							.compareTo(appl.getSmCompanyBasic().getTotalShare()) != 0) {
				if (setNavigationCurrentPage) {
					setNavigationBackToCurrentPage(currentTab, managedBean);
				}
				if(showError){
					throw new EpException(MessageConst.ERROR_722509,
							new String[] { SmCommonUtil.getMessage(
									"label.sm.total.share", null) });
				}
				
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

			String postCode = "";
			for (SmPersonnelAddress addr : pers.getSmPersonnelAddresses()) {
				if (addr.getAddressType() != null
						&& addr.getAddressType()
								.equalsIgnoreCase(
										SupplierManagementConst.SM_PERSONNEL_ADDR_TYPE_PERM)
						&& addr.getSmAddress() != null) {
					postCode = addr.getSmAddress().getPostcode();
					log.debug(" *** Perm Addr Post Code ****" + postCode);
				}
			}
			
			// start #21946
			String myIdentityRespCode;
			if (pers.isEquityOwner() || pers.isDirector()) {
				SmCommonUtil.getInstance().verifyPersonnelWithMyIdentity(pers, postCode, my.ep.constant.sm.SupplierManagementConst.MY_IDENTITY_TYPE_ONLINE, null);
			} else {
				myIdentityRespCode = SmCommonUtil.getInstance().verifyWithMyIdentity(pers.getName(), pers.getIdentificationNo(), postCode,
						my.ep.constant.sm.SupplierManagementConst.MY_IDENTITY_TYPE_ONLINE);
				pers.setIdentityResponseCode(myIdentityRespCode);
			}
			// end #21946
			
		}
		if (!pers.isEquityOwner() && !pers.isDirector()
				&& !pers.isHigherManagement() && !pers.isManagement()
				&& !pers.isProf() && !pers.isSemiProf() && !pers.isClerical()) {
			validUser = false;
			throw new EpException(MessageConst.ERROR_722535);
		}
		for (SmPersonnel per : appl.getSmPersonnels()) {
			if (pers.getPersonnelId() == null && pers.equals(per)) {
				//log.info("****** Same obj before save ");
			} else if (pers.getPersonnelId() != null
					&& pers.getPersonnelId().equals(per.getPersonnelId())) {
				//log.info("**** Same obj without id ******");
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
				// start #21946
				per.setIdentityResidentStatus(resDTO.getResidentStatus());
				// end #21946
			}
			//log.info("**** My identiy output is *****" + resDTO.getStatusCode());
		} catch (Exception e) {
			log.error("**********error calling myidentity api[" + e.getMessage() + "]");
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
					//#12492 start
					RequestContext.getCurrentInstance().execute("bankDialogDuplicate.hide()");
					throw new EpException(MessageConst.ERROR_722543,
							new String[] { bank.getAccountNo(),
							supBank.getPmFinancialOrg().getFinOrgName() });
					//#12492 end
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
		//#31043 start
		if (managedBean.isReviewSiteVisit() && managedBean.isApproverUser()) {
			sec.setSitevisitVerified(isVerifiedAll);
		}
		//#31043 end
		if (managedBean.isShowDockyardPanel()
				|| managedBean.isShowFactoryPanel()
				|| managedBean.isShowStorePanel()
				|| managedBean.isShowWorkShopPanel()) {
			sec.setFactoryVerify(isVerifiedAll);
		}
		if (managedBean.isShowMachineryEquipmentPanel()) {
			sec.setMachineryVerified(isVerifiedAll);
			sec.setManufacturingVerified(isVerifiedAll);
		}
		if (managedBean.getSmAppl().getSupplierType().equals("J")) {
			sec.setAssocBodyVerified(isVerifiedAll);
		}
		if (managedBean.isShowTravelSoftwarePanel()) {
			sec.setSupplierSystemVerified(isVerifiedAll);
		}

		return sec;
	}

	public static int getTickVerifySectionCount(SmApplicationManagedBean bean,
			SmApplSectionReview rev) {
		int retVal = 0;

		if (rev.isBasicVerified()) {
			retVal = retVal + 1;
		}
		if (rev.isOfficeFacilityVerfied()) {
			retVal = retVal + 1;
		}
		if (rev.isStaffVerified()) {
			retVal = retVal + 1;
		}
		if (rev.isTrnsNetworkVerified()) {
			retVal = retVal + 1;
		}
		if (rev.isBranchVerified()) {
			retVal = retVal + 1;
		}
		if (rev.isAssociateVerified()) {
			retVal = retVal + 1;
		}
		if (rev.isCertVerified()) {
			retVal = retVal + 1;
		}
		if (rev.isExperienceVerified()) {
			retVal = retVal + 1;
		}
		if (rev.isCaptialVerified()) {
			retVal = retVal + 1;
		}
		if (rev.isShareholderVerifed()) {
			retVal = retVal + 1;
		}
		if (rev.isEquityCompVerifed()) {
			retVal = retVal + 1;
		}
		if (rev.isPersonnelVerified()) {
			retVal = retVal + 1;
		}
		if (rev.isBankVerified()) {
			retVal = retVal + 1;
		}
		if (rev.isCategoryVerified()) {
			retVal = retVal + 1;
		}
		if (rev.isSitevisitVerified()) {
			retVal = retVal + 1;
		}
		if (rev.isFactoryVerify()) {
			retVal = retVal + 1;
		}
		if (rev.isMachineryVerified()) {
			retVal = retVal + 1;
		}
		if (rev.isAssocBodyVerified()) {
			retVal = retVal + 1;
		}
		if (rev.isSupplierSystemVerified()) {
			retVal = retVal + 1;
		}
		return retVal;
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
		SmCommonUtil.getInstance().setUserDetails(addr);
		return addr;
	}

	/**
	 * Check if company had foreign personnels
	 * 
	 * @param List
	 *            <SmPersonnel> personnels
	 * @return boolean
	 */
	public static boolean isCompanyHasForeignPersonnel(
			List<SmPersonnel> personnels) {
		boolean retVal = false;
		if (!EntityHelper.isCollectionEmpty(personnels)) {
			for (SmPersonnel per : personnels) {
				if (per.getPmCountry() != null
						&& !per.getPmCountry()
								.getCountryCode()
								.equals(SupplierManagementConst.COUNTRY_MALAYSIA_CODE)) {
					retVal = true;
					break;
				}
			}
		}
		return retVal;
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
		//13320-Start
		PmState state = (SmCommonUtil.getInstance().getStateById(addr.getPmState().getStateId()));
		if(state != null){
		if(state.getStateCode().equalsIgnoreCase(SupplierManagementConst.SARAWAK_CODE)){
		addr.setPmDivision(new PmDivision());
		}
		}//13320-End
		addr.setAddress1("");
		addr.setAddress2("");
		addr.setAddress3("");
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
		//13320-Start
		if(facAddr.getPmState().getStateCode().equalsIgnoreCase(SupplierManagementConst.SARAWAK_CODE)){
		if (baseOffAddr.getPmDivision() != null) {
			facAddr.setPmDivision(baseOffAddr.getPmDivision());
		} else {
			facAddr.setPmState(new PmState());
		}
		}
		//13320-End
		if (baseOffAddr.getPmDistrict() != null) {
			facAddr.setPmDistrict(baseOffAddr.getPmDistrict());
		} else {
			facAddr.setPmDistrict(new PmDistrict());
		}
		facAddr.setAddress1(baseOffAddr.getAddress1());
		facAddr.setAddress2(baseOffAddr.getAddress2());
		facAddr.setAddress3(baseOffAddr.getAddress3());
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
		//log.info("****  getStaffList *********" + app.getSupplierType());
		if (app.getSupplierType().equals(
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
		}else{
			SmStaffTotal stf = new SmStaffTotal();
			staffList.add(setDefaultStaffValues(stf, app,
					SupplierManagementConst.STAFF_TYPE_CONTRACTOR));
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
	 * Check overlapping categories.Checks if Same owner exist in different
	 * supplier and has same one or more similar categories
	 * 
	 * @param managedBean
	 *            - SmApplicationManagedBean object
	 */
	public static void checkOverLappingCategories(
			SmApplicationManagedBean managedBean) {
		SmOverlapCategoryVO overlapCategoryVO = null;

		overlapCategoryVO = getOverlapCategoryVO(managedBean.getSmAppl());

		managedBean.setOverLappedCategoryExists(false);
		managedBean.setOverLappedCategoryMsg(StringUtils.EMPTY);
		managedBean.setOverLappedCategoryRemarks(StringUtils.EMPTY);
		//13362
		managedBean.setApplnOverLapRemarks(StringUtils.EMPTY);

		if (overlapCategoryVO != null) {
			managedBean.setOverLappedCategoryExists(overlapCategoryVO
					.isOverLappedCategoryExist());
			managedBean.setOverLappedCategoryMsg(overlapCategoryVO
					.getOverLappedCategoryMsg());
			managedBean.setOverLappedCategoryRemarks(overlapCategoryVO
					.getOverLappedCategoryRemarks());
			managedBean.setOverlapCategoryVOList(overlapCategoryVO
					.getOverlapCategoryVOList());
			//13362
			managedBean.setApplnOverLapRemarks(overlapCategoryVO.getApplnOverLapRemarks());
			managedBean.setOverLappedCategoryExists(true); //#31018
		} else {
			managedBean.setOverlapDocs(null);//#31018
		}
		resetAddedCategoryOverlapStatus(managedBean.getSmAppl(),
				overlapCategoryVO);
	}

	//13362
	public static List<SmBulkApplOverlapCatVO>  checkOverLappingCategoriesMalay(
			SmApplicationManagedBean managedBean) {
		log.info("Inside checkOverLappingCategoriesMalay === ");
		SmOverlapCategoryVO overlapCategoryMalay = null;
		managedBean.getSmAppl().setOverlapLanguage(CommonConstants.LANGUAGE_CODE_MALAY);
		overlapCategoryMalay = getOverlapCategoryVO(managedBean.getSmAppl());

		if (overlapCategoryMalay != null) {
			managedBean.setOverlapCategoryListMalay(overlapCategoryMalay
					.getOverlapCategoryMalayList());
		}
		return overlapCategoryMalay.getOverlapCategoryMalayList();
	}

	public static void validateOverlapCategoryJustificationAndSuppDocs(
			SmApplicationManagedBean bean, String currPage) {
		if (bean.getSmAppl().getApplType()
				.equals(ParameterConst.NEW_SM_APPLN_FOR_APPLCATCODES)
				|| bean.getSmAppl().getApplType()
						.equals(ParameterConst.NEW_SM_APPLN_FOR_MOF)
				|| bean.getSmAppl().getApplType()
						.equals(ParameterConst.NEW_SM_APPLN_FOR_RENEW)
				|| bean.getSmAppl().getApplType()
						.equals(ParameterConst.NEW_SM_APPLN_FOR_BUMI)
				|| bean.getSmAppl().getApplType()
						.equals(ParameterConst.NEW_SM_APPLN_FOR_UPDATE)){
			checkOverLappingCategories(bean);
			if (bean.isOverLappedCategoryExists()) {
				if (bean.isSupplierUser()) {
					validateOverlapCategory(bean, currPage);
				} 
				//#36576 comment this line of code
				/*else {
					for (SmSupplierCategory cat : bean.getSmAppl()
							.getSmSupplierCategories()) {
						if (cat.isOverLapCategory()
								&& (cat.getRecordStatusFlg() == DbValueConst.RECORD_STATUS_PENDING_ACTIVATION || cat
										.getRecordStatusFlg() == DbValueConst.RECORD_STATUS_PENDING_RE_APPROVAL)) {
							if (bean.isPoUser()
									&& cat.getPoDecFlg() != null
									&& cat.getPoDecFlg() == DbValueConst.RECORD_STATUS_ACTIVE) {
								validateOverlapCategory(bean, currPage);
								break;
							} else if (bean.isApproverUser()
									&& cat.getApprvrDecFlg() != null
									&& cat.getApprvrDecFlg() == DbValueConst.RECORD_STATUS_ACTIVE) {
								validateOverlapCategory(bean, currPage);
								break;
							}
						}
					}
				}*/
			}
		}
	}

	private static void validateOverlapCategory(SmApplicationManagedBean bean,
			String currPage) {
		String msg = currPage == null ? MessageConst.ERROR_722654
				: MessageConst.ERROR_722661;
		//#31018 START
		if(bean.isSupplierUser()) { 
			if (EntityHelper.isCollectionEmpty(bean.getOverlapDocs())) {
				throw new EpException(msg);
			} else {
				for (SmCategoryOverlapDoc doc : bean.getOverlapDocs()) {
					if (doc.getPmDocument() == null
						|| StringUtils.isBlank(doc.getFileName())) {
						throw new EpException(msg);
					} else if (StringUtils.isBlank(doc.getOverlapReason())) {
						throw new EpException(msg);
					}
				}
			}
		}//#31018 END
	}

	public static void updateOverlapDocsToSmAppl(SmApplicationManagedBean bean) {
		if (!EntityHelper.isCollectionEmpty(bean.getOverlapDocs())) {
			bean.getSmAppl().setSmCategoryOverlapDocs(
					new ArrayList<SmCategoryOverlapDoc>());
			for (SmCategoryOverlapDoc doc : bean.getOverlapDocs()) {
				if (!StringUtils.isBlank(doc.getOverlapReason())
						&& doc.getPmDocument() != null
						&& !StringUtils.isBlank(doc.getFileName())) {
					bean.getSmAppl().getSmCategoryOverlapDocs().add(doc);
				}
			}
		}
	}

	public static SmOverlapCategoryVO getOverlapCategoryVO(SmAppl smAppl) {
		SmOverlapCategoryVO retVal = null;
		MofRegistrationReqDTO reqDTO = new MofRegistrationReqDTO();
		reqDTO.setSupplierId(smAppl.getSmSupplier().getSupplierId());
		reqDTO.setApplId(smAppl.getApplId());
		
		//13362
		if (smAppl.getOverlapLanguage() != null){
			reqDTO.setLangCode(CommonConstants.LANGUAGE_CODE_MALAY);
			List<Object[]> resultList = SmServiceLookup.getMofRegistrationSO()
					.retrieveOverLappedCategoryDetails(reqDTO).getResultList();
			if (!EntityHelper.isCollectionEmpty(resultList)
					&& !resultList.isEmpty()) {
				retVal = new SmOverlapCategoryVO();
				retVal.setOverlapCategoryMalayList(getOverlapCategoryVOList(
						resultList, smAppl.getSupplierType(), smAppl.getOverlapLanguage()));
			}
		}else{
		reqDTO.setLangCode(new FacesScopes().getUserLocale().getLanguage());
		List<Object[]> resultList = SmServiceLookup.getMofRegistrationSO()
				.retrieveOverLappedCategoryDetails(reqDTO).getResultList();
		if (!EntityHelper.isCollectionEmpty(resultList)
				&& !resultList.isEmpty()) {
			retVal = new SmOverlapCategoryVO();
			retVal.setOverLappedCategoryExist(true);
			retVal.setOverLappedCategoryMsg(SmCommonUtil.getMessage(
					"lbl.sm.category.overlappingMsg", null)
					+ "<br />"
					+ getOverlappedCategoryMsg(resultList));
			Locale loc = new Locale("ms", "MY");
			ResourceBundleUtils.getMessage("lbl.sm.category.overlappingMsg",
					null, ResourceBundleUtils.BUNDLE_COMMON_LABELS, loc);
			retVal.setOverLappedCategoryRemarks(ResourceBundleUtils.getMessage(
					"lbl.sm.category.overlappingMsg", null,
					ResourceBundleUtils.BUNDLE_COMMON_LABELS, loc)
					+ System.lineSeparator()
					+ System.lineSeparator()
					+ getOverlappedCategoryRemarks(resultList));
			retVal.setOverlapCategoryVOList(getOverlapCategoryVOList(
					resultList, smAppl.getSupplierType(), smAppl.getOverlapLanguage())); //ganti dgn overlap lang
			
			//13362 - START setApplnOverlapRemarks
			String companyName = "";
			String mofNo = "";
			String catCode = "";
			String catCodeCombine = "";
			int index = 0;
			
			if(resultList != null){
				for (Object[] arrI : resultList){ 
					catCode = arrI[0].toString() + arrI[1] + arrI[2];
					if("".equalsIgnoreCase(companyName)){
						companyName = arrI[4].toString();
						log.info("MOF NO == "+arrI[5]);
						mofNo = arrI[5] != null ? arrI[5].toString() : "null";
						catCodeCombine += companyName + ", " +  mofNo + ", " + " (";
						index ++;
					}
					if (index == 1){
						catCodeCombine = catCodeCombine + catCode;
					}
					if(arrI[4].toString().equalsIgnoreCase(companyName) && index != 1){
						catCodeCombine = catCodeCombine + ", " + catCode;
					}else{
						companyName = arrI[4].toString();
						log.info("MOF NO == "+arrI[5]);
						mofNo = arrI[5] != null ? arrI[5].toString() : "null";
						
						if (index == 1){
							catCodeCombine = catCodeCombine + "";
							index++;
						}else{
							catCodeCombine = catCodeCombine + ")," + " " + companyName + ", " +  mofNo + ", " + " (" + catCode;
						}
					}	
				}
				catCodeCombine = catCodeCombine + "" + ")";
				retVal.setApplnOverLapRemarks(catCodeCombine);
			}
			//13362 - END
		}
		}
		return retVal;
	}

	private static void resetAddedCategoryOverlapStatus(SmAppl appl,
			SmOverlapCategoryVO vo) {
		for (SmSupplierCategory cat : appl.getSmSupplierCategories()) {
			if (cat.getRecordStatusFlg() == DbValueConst.RECORD_STATUS_PENDING_ACTIVATION
					|| cat.getRecordStatusFlg() == DbValueConst.RECORD_STATUS_PENDING_RE_APPROVAL) {
				cat.setOverLapCategory(false);
				if (vo != null && vo.getOverlapCategoryVOList() != null
						&& cat.getPmCategoryL1() != null
						&& cat.getPmCategoryL2() != null
						&& cat.getPmCategoryL3() != null) {
					String catcode = cat.getPmCategoryL1().getCategoryL1Code()
							+ cat.getPmCategoryL2().getCategoryL2Code()
							+ cat.getPmCategoryL3().getCategoryL3Code();
					for (SmBulkApplOverlapCatVO bulkVO : vo
							.getOverlapCategoryVOList()) {
						if (!StringUtils.isBlank(bulkVO.getCategoryCode())
								&& !StringUtils.isBlank(catcode)
								&& catcode.equals(bulkVO.getCategoryCode())) {
							cat.setOverLapCategory(true);
						}
					}
				}
			}
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
		//log.info("*** Validate the code with directors and equi owners ***** ");
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
			//log.info("*** Over lap record *** " + overLapCategory);
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
					setOverlappedCategoryMsg(msg, count, arr[0], arr[1],
							arr[2], arr[3], arr[4]);
					count++;
				}
			}
			msg.append("</table><br />");
		}
		return msg.toString();
	}

	private static void setOverlappedCategoryMsg(StringBuilder msg, int count,
			Object catCodeL1, Object catCodeL2, Object catCodeL3, Object name,
			Object suppName) {
		msg.append("<tr>");
		msg.append("<td style='width:5%'>").append(count).append(")")
				.append("</td>");
		msg.append("<td style='width:10%'>").append(catCodeL1)
				.append(catCodeL2).append(catCodeL3).append("</td>");
		msg.append("<td style='white-space: normal;'>").append("(")
				.append(name).append(" / ").append(suppName).append(")")
				.append("</td>");
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

	private static List<SmBulkApplOverlapCatVO> getOverlapCategoryVOList(
			List<Object[]> resultList, String suppType, String lang) {
		List<SmBulkApplOverlapCatVO> retVal = new ArrayList<SmBulkApplOverlapCatVO>();

		if (!EntityHelper.isCollectionEmpty(resultList)) {
			SmBulkApplOverlapCatVO voTmp = null;
			SmBulkApplOverlapCatVO voSubTmp = null;
			SmBulkApplOverlapCatVO voSubRemove = null;
			String catCode = StringUtils.EMPTY;
			String isActiveAppl = StringUtils.EMPTY;
			String applStatus = StringUtils.EMPTY;
			String language = null;
			//13362
			if (lang != null){
				language = CommonConstants.LANGUAGE_CODE_MALAY;
			}
			else{
				language = new FacesScopes().getUserLocale().getLanguage();
			}
			boolean isAdd = false;
			boolean isSubAdd = false;
			for (Object[] arr : resultList) {
				catCode = arr[0] + "" + arr[1] + arr[2] + "";
				isActiveAppl = arr[20] + "";
				voTmp = (SmBulkApplOverlapCatVO) EntityHelper
						.getEntityBySinglePropertyFromList(catCode, retVal,
								"categoryCode");
				if (voTmp == null) {
					voTmp = new SmBulkApplOverlapCatVO();
					voTmp.setCategoryCode(catCode);
					voTmp.setCategoryName("(" + arr[17] + "/" + arr[18] + "/"
							+ arr[19] + ")");
					isAdd = true;
				}

				if (isActiveAppl.equals("1")) {
					SmWorkflowStatus workflow = SmCommonUtil
							.getCurrentActiveWokFlowStatus(Long
									.parseLong(arr[22] + ""));
					if (workflow != null
							&& workflow.getPmStatus() != null
							&& !EntityHelper.isCollectionEmpty(workflow
									.getPmStatus().getPmStatusDescs())) {
						for (PmStatusDesc desc : workflow.getPmStatus()
								.getPmStatusDescs()) {
							if (!StringUtils.isBlank(desc.getLanguageCode())
									&& desc.getLanguageCode().equals(language)) {
								applStatus = desc.getStatusName();
							}
						}
					}
				}

				if (EntityHelper.isCollectionEmpty(voTmp.getOverlapInfoList())) {
					voTmp.setOverlapInfoList(new ArrayList<SmBulkApplOverlapCatVO>());
				}

				voSubTmp = getOverlapVOByRegNoAndIdNo(arr[7] + "",
						arr[16] + "", voTmp.getOverlapInfoList());

				if (voSubTmp == null) {
					isSubAdd = true;
				} else {
					if (isActiveAppl.equals("1") && !voSubTmp.isActiveAppl()) {
						voSubRemove = voSubTmp;
						isSubAdd = true;
					}
				}

				if (isSubAdd) {
					voSubTmp = new SmBulkApplOverlapCatVO();
					voSubTmp.setActiveAppl(isActiveAppl.equals("1") ? true
							: false);
					voSubTmp.setPersonnelName(arr[3] + "");
					voSubTmp.setIdentificationNo(arr[16] + "");
					voSubTmp.setRole(getRoleName(arr[9] + "", arr[10] + "",
							arr[11] + "", arr[15] + "", arr[12] + "", arr[13]
									+ "", arr[14] + "", suppType, lang));
					voSubTmp.setCompanyName(arr[4] + "");
					voSubTmp.setMofNo(arr[5] + "");
					if (StringUtils.isBlank(voSubTmp.getMofNo())) {
						voSubTmp.setMofNo(SmCommonUtil.getMessage("lbl.txt.na",
								null));
					}
					voSubTmp.setExpDate(arr[6] + "");
					if (StringUtils.isBlank(voSubTmp.getExpDate())) {
						voSubTmp.setExpDate(SmCommonUtil.getMessage(
								"lbl.txt.na", null));
					}
					voSubTmp.setRegNo(arr[7] + "");
					voSubTmp.setApplNo(arr[8] + "");
					voSubTmp.setApplStatus(StringUtils.isBlank(applStatus) ? arr[21]
							+ ""
							: applStatus);
					voTmp.getOverlapInfoList().add(voSubTmp);
					isSubAdd = false;
				}

				if (voSubRemove != null) {
					voTmp.getOverlapInfoList().remove(voSubRemove);
				}

				if (isAdd) {
					retVal.add(voTmp);
					isAdd = false;
				}

			}
		}
		return retVal;
	}

	private static SmBulkApplOverlapCatVO getOverlapVOByRegNoAndIdNo(
			String regNo, String idNo, List<SmBulkApplOverlapCatVO> overlapLists) {
		SmBulkApplOverlapCatVO retVal = null;
		if (!EntityHelper.isCollectionEmpty(overlapLists)) {
			for (SmBulkApplOverlapCatVO vo : overlapLists) {
				if (vo.getRegNo() != null && regNo != null
						&& vo.getRegNo().equals(regNo)
						&& vo.getIdentificationNo() != null && idNo != null
						&& vo.getIdentificationNo().equals(idNo)) {
					retVal = vo;
					break;
				}
			}
		}
		return retVal;
	}

	private static String getRoleName(String equityOwner, String director,
			String higherManagement, String clerical, String management,
			String prof, String semiProf, String suppType, String lang) {
		StringBuilder sb = new StringBuilder();
		//13362
		if (lang != null){
			if (equityOwner.equals("1")) {
				sb.append(
						SmCommonUtil.getMessageMalay(
								SupplierManagementConst.PERSONNEL_EQUI_OWNER, null))
						.append(",");
			}
			if (director.equals("1")) {
				sb.append(
						SmCommonUtil.getMessageMalay(
								SupplierManagementConst.PERSONNEL_DIR_ROLENAME,
								null)).append(",");
			}
			if (higherManagement.equals("1")) {
				sb.append(
						SmCommonUtil.getMessageMalay(
								SupplierManagementConst.PERSONNEL_HGHR_ROLENAME,
								null)).append(",");
			}
			if (management.equals("1")) {
				sb.append(
						SmCommonUtil
								.getMessageMalay(
										SupplierManagementConst.PERSONNEL_MANAGEMENT_ROLENAME,
										null)).append(",");
			}
			if (clerical.equals("1")) {
				if (suppType.equals(SupplierManagementConst.STYPE_CONSULTANT)) {
					sb.append(
							SmCommonUtil
									.getMessageMalay(
											SupplierManagementConst.PERSONNEL_CLERICAL_STAFF_ROLENAME,
											null)).append(",");
				} else {
					sb.append(
							SmCommonUtil
									.getMessageMalay(
											SupplierManagementConst.PERSONNEL_STAFF_ROLENAME,
											null)).append(",");
				}
			}
			if (prof.equals("1")) {
				sb.append(
						SmCommonUtil
								.getMessageMalay(
										SupplierManagementConst.PERSONNEL_PROF_STAFF_ROLENAME,
										null)).append(",");
			}
			if (semiProf.equals("1")) {
				sb.append(
						SmCommonUtil
								.getMessageMalay(
										SupplierManagementConst.PERSONNEL_SEMIPROF_STAFF_ROLENAME,
										null)).append(",");
			}
			}else{
		if (equityOwner.equals("1")) {
			sb.append(
					SmCommonUtil.getMessage(
							SupplierManagementConst.PERSONNEL_EQUI_OWNER, null))
					.append(",");
		}
		if (director.equals("1")) {
			sb.append(
					SmCommonUtil.getMessage(
							SupplierManagementConst.PERSONNEL_DIR_ROLENAME,
							null)).append(",");
		}
		if (higherManagement.equals("1")) {
			sb.append(
					SmCommonUtil.getMessage(
							SupplierManagementConst.PERSONNEL_HGHR_ROLENAME,
							null)).append(",");
		}
		if (management.equals("1")) {
			sb.append(
					SmCommonUtil
							.getMessage(
									SupplierManagementConst.PERSONNEL_MANAGEMENT_ROLENAME,
									null)).append(",");
		}
		if (clerical.equals("1")) {
			if (suppType.equals(SupplierManagementConst.STYPE_CONSULTANT)) {
				sb.append(
						SmCommonUtil
								.getMessage(
										SupplierManagementConst.PERSONNEL_CLERICAL_STAFF_ROLENAME,
										null)).append(",");
			} else {
				sb.append(
						SmCommonUtil
								.getMessage(
										SupplierManagementConst.PERSONNEL_STAFF_ROLENAME,
										null)).append(",");
			}
		}
		if (prof.equals("1")) {
			sb.append(
					SmCommonUtil
							.getMessage(
									SupplierManagementConst.PERSONNEL_PROF_STAFF_ROLENAME,
									null)).append(",");
		}
		if (semiProf.equals("1")) {
			sb.append(
					SmCommonUtil
							.getMessage(
									SupplierManagementConst.PERSONNEL_SEMIPROF_STAFF_ROLENAME,
									null)).append(",");
		}
			}
		
		return sb.length() > 0 ? StringUtils.removeEnd(sb.toString(), ",")
				: StringUtils.EMPTY;
	}

	/**
	 * Validate supporting docs decision by PO.
	 * 
	 * @param managedBean
	 *            - SmApplicationManagedBean object
	 */
	public static void validateSupportingDocsDecision(
			SmApplicationManagedBean managedBean) {
		if (!SmApplicationUtil.isAllSuppDocAlreadyVerified(managedBean
				.getSmAppl().getSmApplSupportDocs(), managedBean.getSmAppl()
				.getSupportingDocMode(), managedBean.isApproverUser())) {
			throw new EpException(MessageConst.ERROR_722559);
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
		validateCategoriesDecision(managedBean);
		validateSupportingDocsDecision(managedBean);
	}

	public static void validateCategoriesDecision(
			SmApplicationManagedBean managedBean) {
		boolean isAtLeastOneCatApproved = false;
		boolean isPendingApprovedCatExist = false;

		for (SmSupplierCategory cat : managedBean.getSmAppl()
				.getSmSupplierCategories()) {
			if (cat.getRecordStatusFlg().compareTo(
					SupplierManagementConst.PENDING_ACT_STATUS) == 0
					|| cat.getRecordStatusFlg().compareTo(
							SupplierManagementConst.PENDING_RE_APPROVAL_STATUS) == 0) {

				isPendingApprovedCatExist = true;

				if (managedBean.isPoUser() && cat.getPoDecFlg() == null) {
					throw new EpException(MessageConst.ERROR_722558,
							new String[] { SmCommonUtil.getMessage(
									"lbl.sm.catCodes.poAct", null) });
				} else if (managedBean.isApproverUser()
						&& cat.getApprvrDecFlg() == null) {
					throw new EpException(MessageConst.ERROR_722558,
							new String[] { SmCommonUtil.getMessage(
									"lbl.sm.catCodes.appAct", null) });
				} else {
					// at least one category is needed to be approved
					if ((managedBean.isPoUser() && cat.getPoDecFlg() != null && cat
							.getPoDecFlg() == 1)
							|| (managedBean.isApproverUser()
									&& cat.getApprvrDecFlg() != null && cat
									.getApprvrDecFlg() == 1)) {
						isAtLeastOneCatApproved = true;
					}
				}

			}
		}

		// no category at all approved
		if (isPendingApprovedCatExist && !isAtLeastOneCatApproved) {
			assignValReqdToFalse(managedBean);	//#35059
			throw new EpException(MessageConst.ERROR_722655);
		}
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
			if ((managedBean.isPoUser() && doc.getVerifiedFlg() == null)) {
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
			List<SmSupplierCategory> catList = new ArrayList<>();
			List<SmSupplierCategory> apprvList = new ArrayList<>();
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
					//30766 - START
					//29823 - start
					//#34098 - START 
					if(managedBean.getCurrentStatus() != null 
							&& (managedBean.getCurrentStatus().equalsIgnoreCase("Pending Review Site Visit Report - After Approval") ||
							    managedBean.getCurrentStatus().equalsIgnoreCase("Menunggu Laporan Lawatan Tapak - Selepas Diluluskan"))
							&& !managedBean.getSmAppl().getApplType().equals(SupplierManagementConst.APPL_TYPE_BUMI)
							&& !managedBean.getSmAppl().getApplType().equals(SupplierManagementConst.APPL_TYPE_CATEGORY)){
						catList.add(cat);
					}else if(managedBean.getCurrentStatus() != null 
							&& (managedBean.getCurrentStatus().equalsIgnoreCase("Pending Review Site Visit Report - After Approval") ||
								managedBean.getCurrentStatus().equalsIgnoreCase("Menunggu Laporan Lawatan Tapak - Selepas Diluluskan"))
							&& managedBean.getSmAppl().getApplType().equals(SupplierManagementConst.APPL_TYPE_CATEGORY)){
						if(cat.getApplRefId().equals(cat.getSmAppl().getApplId())){
							catList.add(cat);
						}else{
							apprvList.add(cat);
						}
					//#34098 - END 
					}else{
						apprvList.add(cat);
					}
					//29823 - end
					//30766 - END
			
					//30865, 30921 - Starts
				} else if (cat.getRecordStatusFlg().compareTo(DbValueConst.RECORD_STATUS_CANCELLED) == 0){
					addValidCategoryWithDAType(cat, apprvList);	//#38984
					//#32298, #32810, #32298 DA cancel manufacture alert warning - end - <26238>
				}
				//30865, 30921  - Ends
			}
			setCancelCategoryCatCodes(managedBean.getSmAppl().getSmSupplier(), catList);
			if (managedBean.getActiveCategoryList() != null
					&& !managedBean.getActiveCategoryList().isEmpty()) {
				managedBean.getActiveCategoryList().clear();
			}
			if (managedBean.getApprovedCategoryList() != null
					&& !managedBean.getApprovedCategoryList().isEmpty()) {
				managedBean.getApprovedCategoryList().clear();
			}
			sortSupplierCategory(apprvList);
			sortSupplierCategory(catList);
			managedBean.setActiveCategoryList(catList);
			managedBean.setApprovedCategoryList(apprvList);
		}
	}
	
	//#26238 - 38984
	private static void addValidCategoryWithDAType(SmSupplierCategory cat, List<SmSupplierCategory> catList){
		if (cat == null || cat.getDisciplinaryActionId() == null){
			return;
		}
		SmDisciplinaryAction da = retrieveActiveSmDisciplinaryActionByDisciplinaryId(cat.getDisciplinaryActionId());
		if(da == null || da.getRecordStatusFlg() == null || 
				da.getRecordStatusFlg() != DbValueConst.RECORD_STATUS_ACTIVE){
			return;
		}
		
		cat.setCancelCategory(true);
		if(da.isCancelCategory() || da.isBumiputeraCancel()){
			cat.setTypeDisciplinaryAction(SupplierManagementConst.SM_BASIC_DISCIPLINARY_ACTION_CANCEL_REG);
		}else if(da.isManufacturerCancel()){
			cat.setTypeDisciplinaryAction(SupplierManagementConst.SM_BASIC_DISCIPLINARY_ACTION_CANCEL_MANUFACTURE);
		}
		catList.add(cat);
	}
	
	//#26238
	private static void setCancelCategoryCatCodes(SmSupplier supplier, 
			List<SmSupplierCategory> suppCategories){
		if(supplier == null || EntityHelper.isCollectionEmpty(supplier.getSmDisciplinaryActions())
				|| EntityHelper.isCollectionEmpty(suppCategories)){
			return;
		}
		List<Object[]> cancelCat = SmApplicationUtil.retrieveCancelCategoryBySupplier(supplier);
		if(cancelCat == null){
			return;
		}
		Date currentDate = new Date();
		boolean isAfterToday = currentDate.after(supplier.getSmDisciplinaryActions().get(0).getEndDate());
		for(SmSupplierCategory cat : suppCategories){
			for(Object[] arr : cancelCat){
				if(!arr[2].toString().equals(cat.getPmCategoryL3().getCategoryL3Id().toString())){
					continue;
				}
				//29952 
				cat.setCancelCategory(!isAfterToday);
			}
		}
	}

	private static void assignExistingAndAdditionalCategoryList(
			SmApplicationManagedBean managedBean) {
		List<SmSupplierCategory> addtnlList = new ArrayList<SmSupplierCategory>();
		List<SmSupplierCategory> existingList = new ArrayList<SmSupplierCategory>();
		//#30766 & #34209 - START - to remove category with deleted status in the list - fix issue active category show in addcat list
		List<SmSupplierCategory> delCatList = new ArrayList<SmSupplierCategory>();
		for(SmSupplierCategory filtCat : managedBean.getSmAppl().getSmSupplier()
				.getSmAppl().getSmSupplierCategories()){
			if(filtCat.getRecordStatusFlg() == DbValueConst.RECORD_STATUS_DELETED){
				delCatList.add(filtCat);
			}
		}
		managedBean.getSmAppl().getSmSupplier().getSmAppl().getSmSupplierCategories().removeAll(delCatList);
		//#30766 & #34209 - END
		//28775 - Site Visit Grouping Enhancement -28775
		List<Integer> validStatus = Arrays.asList(DbValueConst.RECORD_STATUS_ACTIVE,
				DbValueConst.RECORD_STATUS_PENDING_RE_APPROVAL, DbValueConst.RECORD_STATUS_PENDING_ACTIVATION);
		for (SmSupplierCategory cat : managedBean.getSmAppl()
				.getSmSupplierCategories()) {
			if (validStatus.contains(cat.getRecordStatusFlg()) && cat.getPmCategoryL3() != null) {

				SmSupplierCategory tmp = (SmSupplierCategory) EntityHelper
						.getEntityBySinglePropertyFromList(cat
								.getPmCategoryL3().getCategoryL3Id(),
								managedBean.getSmAppl().getSmSupplier()
										.getSmAppl().getSmSupplierCategories(),
								"pmCategoryL3");
				if (tmp != null
						&& (tmp.getRecordStatusFlg() == DbValueConst.RECORD_STATUS_ACTIVE
								|| tmp.getRecordStatusFlg() == DbValueConst.RECORD_STATUS_PENDING_RE_APPROVAL 
								|| tmp.getRecordStatusFlg() == DbValueConst.RECORD_STATUS_PENDING_ACTIVATION)) {
					if(cat.getRecordStatusFlg().compareTo(SupplierManagementConst.PENDING_ACT_STATUS) == 0)
						addtnlList.add(cat);
					else
						existingList.add(cat);
				} else if (cat.getRecordStatusFlg().compareTo(
						SupplierManagementConst.PENDING_ACT_STATUS) == 0
						|| cat.getRecordStatusFlg()
								.compareTo(
										SupplierManagementConst.PENDING_RE_APPROVAL_STATUS) == 0) {
					addtnlList.add(cat);
				}
				//30865 - Starts
			} else if (cat.getRecordStatusFlg().compareTo(DbValueConst.RECORD_STATUS_CANCELLED) == 0 
					|| cat.getRecordStatusFlg().compareTo(DbValueConst.RECORD_STATUS_DELETED) == 0){
				addValidCategoryWithDAType(cat, existingList); //#38984
			}
				//30865 - Ends
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
		sortSupplierCategory(addtnlList);
		managedBean.setActiveCategoryList(addtnlList);
		managedBean.setApprovedCategoryList(existingList);
	}
	
	//14150

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
		
		
	}
	
	/**
	 * Sort SmSupplierCategory by category type and category level type
	 * 
	 * @param apprvList
	 */
	public static void sortSupplierCategoryType(List<SmSupplierCategory> apprvList) {
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
	//14150
	public static void sortbyDate(List<SmSupplierCategory> catList){
		// sort created date descendent
				if(!EntityHelper.isCollectionEmpty(catList)){
					// sort created date asc
					Collections.sort(catList, new Comparator<SmSupplierCategory>() {
						public int compare(SmSupplierCategory o1, SmSupplierCategory o2) {
							if (o2.getApprovedDate() == null || o1.getApprovedDate() == null)
								return 0;
							return o1.getApprovedDate().compareTo(o2.getApprovedDate());
						}
					});
				}
	}
	/**
	 * Sort personnels by role in company
	 * 
	 * @param SmAppl
	 *            appl
	 */
	public static void sortPersonnelByRoleInCompany(SmAppl appl) {
		if (appl != null
				&& !EntityHelper.isCollectionEmpty(appl.getSmPersonnels())) {

			for (SmPersonnel per : appl.getSmPersonnels()) {
				if (per.isEquityOwner()) {
					if (per.isDirector() && per.isHigherManagement()) {
						per.setSort(1);
					} else if (per.isDirector()) {
						per.setSort(2);
					} else if (per.isHigherManagement()) {
						per.setSort(3);
					}
				} else if (!per.isEquityOwner() && per.isDirector()) {
					if (per.isHigherManagement()) {
						per.setSort(4);
					} else {
						per.setSort(5);
					}
				} else if (!per.isEquityOwner() && !per.isDirector()
						&& per.isHigherManagement()) {
					if (per.isManagement()) {
						per.setSort(6);
					} else {
						per.setSort(7);
					}
				} else if (!per.isEquityOwner() && !per.isDirector()
						&& !per.isHigherManagement() && per.isManagement()) {
					per.setSort(8);
				} else if (!per.isEquityOwner() && !per.isDirector()
						&& !per.isHigherManagement() && !per.isManagement()
						&& per.isProf()) {
					per.setSort(9);
				} else if (!per.isEquityOwner() && !per.isDirector()
						&& !per.isHigherManagement() && !per.isManagement()
						&& !per.isProf() && per.isSemiProf()) {
					per.setSort(10);
				} else if (!per.isEquityOwner() && !per.isDirector()
						&& !per.isHigherManagement() && !per.isManagement()
						&& !per.isProf() && !per.isSemiProf()
						&& per.isClerical()) {
					per.setSort(11);
				} else {
					per.setSort(12);
				}
			}

			// sort by name
			Collections.sort(appl.getSmPersonnels(),
					new Comparator<SmPersonnel>() {
						@Override
						public int compare(final SmPersonnel s1,
								final SmPersonnel s2) {
							return (s1.getName()).compareTo(s2.getName());
						}
					});

			// sort by sort
			Collections.sort(appl.getSmPersonnels(),
					new Comparator<SmPersonnel>() {
						@Override
						public int compare(final SmPersonnel s1,
								final SmPersonnel s2) {
							return (s2.getSort() > s1.getSort() ? -1 : (s1
									.getSort() == s2.getSort() ? 0 : 1));
						}
					});

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
		//log.info("*** This is resubmit to get po ****** ");
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
			//log.info("******** The user id for PO from the data ******* "
					//+ resDTO.getWorkflowStatus().getCreatedBy());
			UserReqDTO userReqDTO = new UserReqDTO();
			userReqDTO.setUserId(resDTO.getWorkflowStatus().getCreatedBy());
			IUserSO userSO = (IUserSO) BusinessDelegate.getInstance()
					.getServiceObject("pm", IUserSO.class);
			UserResDTO userResDTO = userSO.retrieveUserByUserId(userReqDTO);
			if (userResDTO != null && userResDTO.getPmUser() != null) {

				// check whether the PO is still active
				PmUser poUser = userResDTO.getPmUser();
				if (poUser.getRecordStatusFlg() == DbValueConst.RECORD_STATUS_ACTIVE
						&& !EntityHelper.isCollectionEmpty(poUser
								.getPmUserOrgs())) {
					for (PmUserOrg userOrg : poUser.getPmUserOrgs()) {
						if (userOrg.getRecordStatusFlg() == DbValueConst.RECORD_STATUS_ACTIVE
								&& !EntityHelper.isCollectionEmpty(userOrg
										.getPmUserRoles())) {
							for (PmUserRole userRole : userOrg.getPmUserRoles()) {
								if (userRole.getRecordStatusFlg() == DbValueConst.RECORD_STATUS_ACTIVE
										&& userRole.getPmRole() != null
										&& userRole
												.getPmRole()
												.getRoleCode()
												.equals(RoleCode.UPS_PO.value())) {
									poLogin = userResDTO.getPmUser()
											.getLoginId();
									break;
								}
							}
						}
					}
				}
				//log.info("******* After retrieve po details ******** ");
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
		//log.info("*** This is resubmit to get po from MOF API ****** ");
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
				//#32627 START
				.append(",")
				.append(SupplierManagementConst.PM_STATUS_REJECTED_BY_SYSTEM_SDO_INQUIRY)
				.append(",")
				.append(SupplierManagementConst.PM_STATUS_REJECTED_BY_SYSTEM_PO_INQUIRY)
				.append(",")
				.append(SupplierManagementConst.PM_STATUS_AUTOREJECT_UNSUBMIT_OFFLINE_DOCUMENT)
				//#32627 END
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
		//#27165
		 managedBean.setDisAllowOwnerEdit(false);
		if (isMofAccEffDatePassedCurrDate(managedBean.getSmSupplier())) {
			managedBean.setDisAllowOwnerEdit(true);
		}
		//managedBean.setDisAllowOwnerEdit(false);
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
			//20200615@Jacky - #28196 - Start
			// 28543 - Prevent another bumi application
//			if(smSupplier.isBumi()){
//				smSupplier.setBumi(false);
//			}
			//20200615@Jacky - #28196 - End
			// check if dont have supplier admin
			validateDontHaveSuppAdmin(smSupplier.getSmAppl());

			// check if already had rejected application within 60 days (and
			// created date after latest application created date), can't
			// proceed. Need to used resubmit link.
			SmAppl rejectedAppl = getLatesRejectedApplicationBySuppIdAndAppType(
					supplierId, applicationType);
			if (rejectedAppl != null
					&& rejectedAppl.getCreatedDate()
							.after(resDto.getSmSupplier().getSmAppl()
									.getCreatedDate())
					&& rejectedAppl.getApplType().equals(applicationType)
					&& isApplPORejectedAfterApprovelSV(rejectedAppl)) { //#34483
				if (isRejectedApplValidForResubmission(rejectedAppl)) {
					result = true;
					throw new EpException(MessageConst.ERROR_722643);
				}
			}

			//log.info("validate before proceed regNo:" + smSupplier.getRegNo()
					//+ " isBumi:" + smSupplier.isBumi());

			// not allowed to proceed if not answer questionaire except for
			// bumi application and basic supplier admin role (happen for
			// expired mof downgrade to basic)
			Long userId = new FacesScopes().getUserSessionBean().getUserId();
			SmPersonnel suppAdmin = null;
			if (userId != null
					&& smSupplier.getSmAppl().getSmPersonnels() != null) {
				suppAdmin = (SmPersonnel) EntityHelper
						.getEntityBySinglePropertyFromList(userId, smSupplier
								.getSmAppl().getSmPersonnels(), "pmUser");
			}
			//#26238
			validateBeforeProceedDA(suppAdmin, applicationType);
			validateBumiDA(smSupplier, applicationType);
			
			if (smSupplier.getSmAppl() != null
					&& !smSupplier.getSmAppl().isQuestionnaire()
					&& !StringUtils.isBlank(smSupplier.getSmAppl()
							.getApplType())
					&& !smSupplier
							.getSmAppl()
							.getApplType()
							.equalsIgnoreCase(
									ParameterConst.NEW_SM_APPLN_FOR_BUMI)
					&& suppAdmin != null
					&& !StringUtils.isBlank(suppAdmin.getEpRole())
					&& !suppAdmin.getEpRole().equals(
							RoleCode.BASIC_SUPPLIER_ADMIN.value())) {
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
			// 25996 start
			int inactiveMofAcc = 0;
			// 25996 end
			for (SmMofAccount account : accounts) {
				if (account.getSupplierType().equalsIgnoreCase(
						smSupplier.getSmAppl().getSupplierType())) {
					//SSM INTEGRATION #27111
					if(account.getRecordStatusFlg().compareTo(
								DbValueConst.RECORD_STATUS_ACTIVE) == 0 || account.getRecordStatusFlg().compareTo(
										DbValueConst.RECORD_STATUS_EXPIRED) == 0){
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
							//#36594 - Start
							int renewMinTime = SmServiceLookup.getSmRuleSO().retrieveRenewalPeriodForAppl("");
							int renewalPeriod = SmServiceLookup.getSmRuleSO().retrieveRenewalPeriodForAppl(ParameterConst.NEW_SM_APPLN_FOR_RENEW);
							if (diffDays > renewMinTime) { //#36391
								result = true;
								throw new EpException(MessageConst.ERROR_722501, new String[] { String.valueOf(renewalPeriod), String.valueOf(renewMinTime)});
							//#36594 - End
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
					}else if(account.getRecordStatusFlg().compareTo(
								DbValueConst.RECORD_STATUS_DELETED) == 0){
						// 25996 start
						inactiveMofAcc++;
						// 25996 end
					}
				}
			}
			
			// 25996 start
			// Only throw exception if supplier have no active mof account
			if (inactiveMofAcc == accounts.size()) {
				throw new EpException(MessageConst.ERROR_722502);
			}
			// 25996 end
						
			// 11231: start - check maximum limit 30 categories per supplier
			if (applicationType.equalsIgnoreCase(ParameterConst.NEW_SM_APPLN_FOR_APPLCATCODES)) {
				int maxCatSize = SmRuleHelper.getInstance().getMaxCategoryAllowed();
				List<SmSupplierCategory> suppCategory = new ArrayList<>();
				for (SmSupplierCategory cat : smSupplier.getSmAppl().getSmSupplierCategories()) {
					if (DbValueConst.RECORD_STATUS_ACTIVE == cat.getRecordStatusFlg()) {
						suppCategory.add(cat);
					}
				}
				//log.info("total category:" + suppCategory.size());
				if (suppCategory.size() >= maxCatSize) {
					throw new EpException(MessageConst.ERROR_722675);
				}
			}
			// 11231: end

		}
		return result;
	}
	
	//#26238 - start
	private static void validateBeforeProceedDA(SmPersonnel suppAdmin, String applType){
		if(suppAdmin == null){
			return;
		}
		boolean types = applType.equalsIgnoreCase(ParameterConst.NEW_SM_APPLN_FOR_BUMI)
				|| applType.equalsIgnoreCase(ParameterConst.NEW_SM_APPLN_FOR_APPLCATCODES)
				|| applType.equalsIgnoreCase(ParameterConst.NEW_SM_APPLN_FOR_RENEW);
		boolean cancelAdmin = suppAdmin.isSuspendRegistration() 
				|| suppAdmin.isCancelRegistration() || suppAdmin.isBlacklisted();
		if(types && cancelAdmin){
			throw new EpException(SmCommonUtil.getMessage(
					"lbl.sm.not.allowed.to.proceed.due.to.disciplinary.action", null));
		}
	}
	
	private static void validateBumiDA(SmSupplier supplier, String applType){
		if(!applType.equalsIgnoreCase(ParameterConst.NEW_SM_APPLN_FOR_BUMI)){
			return;
		}
		List<SmDisciplinaryAction> daList = retrieveActiveSmDisciplinaryActionBySupplier(supplier);
		if(EntityHelper.isCollectionEmpty(daList)){
			return;
		}
		for(SmDisciplinaryAction disc : daList){
			if(disc.isBumiputeraCancel()){
				throw new EpException(SmCommonUtil.getMessage(
						"lbl.sm.not.allowed.to.proceed.due.to.disciplinary.action", null));
			}
		}
	}
	//#26238 - end

	private static void validateDontHaveSuppAdmin(SmAppl appl) {
		boolean valid = false;
		//#34483 START
		PersonnelReqDTO reqDTO = new PersonnelReqDTO();
		reqDTO.setApplNo(appl.getApplNo());
		List<SmPersonnel> personnels = SmServiceLookup.getPersonnelSO()
				.retrievePersonnelsByApplNo(reqDTO).getSmPersonnelList();

		if (personnels != null) {
			for (SmPersonnel per : personnels) { //#34483 END
				if (!StringUtils.isBlank(per.getEpRole())
						&& per.getPmUser() != null
						&& per.getPmUser().getUserId() != null
						&& my.ep.constant.sm.SupplierManagementConst.SUPPLIER_ADMIN_ROLES
								.contains(per.getEpRole())
						&& per.getRecordStatusFlg() == DbValueConst.RECORD_STATUS_ACTIVE) {
					valid = true;
				}
			}
			if (!valid) {
				throw new EpException(MessageConst.ERROR_700124);
			}
		}
	}

	public static void validateDontHavePremise(SmAppl appl) {
		if (appl != null
				&& EntityHelper.isCollectionEmpty(appl.getSmFacilities())) {
			throw new EpException(MessageConst.ERROR_722670);
		}
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
	
	public static boolean hasActiveTransactionForContractManagement(SmSupplier smSupplier, String accountNo, Long supplierId) { //#30693

		IMofRegistrationSO mofRegistrationSO = (IMofRegistrationSO) BusinessDelegate
				.getInstance().getServiceObject("sm", IMofRegistrationSO.class);

		MofRegistrationReqDTO reqDTO = new MofRegistrationReqDTO();
		//#30693--START
		if(smSupplier.getSupplierId()!= null){
			log.info("smSupplier.getSupplierId()...ct");
			reqDTO.setSupplierId(smSupplier.getSupplierId());
		}else{
			log.info("SupplierId...ct");
			reqDTO.setSupplierId(supplierId);
		}
		//#30693--END
		reqDTO.setAccountNo(accountNo);

		MofRegistrationResDTO resDTO = mofRegistrationSO
				.hasActiveTransactionsForContractManagement(reqDTO);

		return resDTO.isHasActiveTransaction();
	}
	
	public static boolean hasActiveTransactionForFulfilment(SmSupplier smSupplier, String accountNo, Long supplierId) { //#30693

		IMofRegistrationSO mofRegistrationSO = (IMofRegistrationSO) BusinessDelegate
				.getInstance().getServiceObject("sm", IMofRegistrationSO.class);

		MofRegistrationReqDTO reqDTO = new MofRegistrationReqDTO();
		//#30693--START
		if(smSupplier.getSupplierId()!= null){
			log.info("smSupplier.getSupplierId()...fl");
			reqDTO.setSupplierId(smSupplier.getSupplierId());
		}else{
			log.info("SupplierId...fl");
			reqDTO.setSupplierId(supplierId);
		}
		//#30693--END
		reqDTO.setAccountNo(accountNo);

		MofRegistrationResDTO resDTO = mofRegistrationSO
				.hasActiveTransactionsForFulfilment(reqDTO);

		return resDTO.isHasActiveTransaction();
	}

	public static boolean hasOtherActiveTransactions(SmSupplier smSupplier) {

		IMofRegistrationSO mofRegistrationSO = (IMofRegistrationSO) BusinessDelegate
				.getInstance().getServiceObject("sm", IMofRegistrationSO.class);

		MofRegistrationReqDTO reqDTO = new MofRegistrationReqDTO();
		reqDTO.setSupplierId(smSupplier.getSupplierId());

		MofRegistrationResDTO resDTO = mofRegistrationSO
				.hasOtherActiveTransactionsForSupplier(reqDTO);

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
		IMofRegistrationSO mofRegistrationSO = (IMofRegistrationSO) BusinessDelegate
				.getInstance().getServiceObject("sm", IMofRegistrationSO.class);
		MofRegistrationReqDTO reqDTO = new MofRegistrationReqDTO();
		MofRegistrationResDTO resDTO = new MofRegistrationResDTO();
		reqDTO.setSupplierId(smSupplier.getSupplierId());
		resDTO = mofRegistrationSO.checkActiveApplicationBySupplierId(reqDTO);
		return resDTO.isHasActiveTransaction();
	}
	
	//26238 - start
	/**
	 * 
	 * @param smSupplier
	 * @return
	 */
	public static boolean checkActiveSmApplForSuppDA(SmSupplier smSupplier) {
		MofRegistrationReqDTO reqDTO = new MofRegistrationReqDTO();
		reqDTO.setSupplierId(smSupplier.getSupplierId());
		MofRegistrationResDTO resDTO = SmServiceLookup.getMofRegistrationSO().
				checkActiveApplicationBySupplierIdDA(reqDTO);
		return resDTO.isHasActiveTransaction();
	}
	//26238 - end

	/**
	 * Check if there is any active Cancel MOF appl for supplier.
	 * 
	 * @param smSupplier
	 *            - SmSupplier object
	 * @return true, if successful
	 */
	public static boolean checkActiveCancelSmApplForSupp(SmSupplier smSupplier) {
		IMofRegistrationSO mofRegistrationSO = (IMofRegistrationSO) BusinessDelegate
				.getInstance().getServiceObject("sm", IMofRegistrationSO.class);
		MofRegistrationReqDTO reqDTO = new MofRegistrationReqDTO();
		MofRegistrationResDTO resDTO = new MofRegistrationResDTO();
		reqDTO.setSupplierId(smSupplier.getSupplierId());
		resDTO = mofRegistrationSO
				.checkActiveCancelApplicationBySupplierId(reqDTO);
		return resDTO.isHasActiveTransaction();
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
		//log.info("** getSupplierType type ***"
				//+ managedBean.getSmAppl().getSupplierType());
		//log.info("** getApplCategoryType type ***"
				//+ managedBean.getSmAppl().getApplCategoryType());
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
			reqDTO.setPaidCapital(managedBean.getSmAppl().getSmCompanyBasic()
					.getPaidUpCapital());
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
		//log.info("*** Bumi Percent Sent for Category L1 ***** " + bumiPerc);
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
			reqDTO.setPaidCapital(managedBean.getSmAppl().getSmCompanyBasic()
					.getPaidUpCapital());
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
		//log.info("*** Bumi Percent Sent for Category L2 ***** " + bumiPerc);
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
		//log.info("** getSupplierType type ***"
				//+ managedBean.getSmAppl().getSupplierType());
		//log.info("** getApplCategoryType type ***"
				//+ managedBean.getSmAppl().getApplCategoryType());
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
			reqDTO.setPaidCapital(managedBean.getSmAppl().getSmCompanyBasic()
					.getPaidUpCapital());
		}
		Integer bumiPerc = new Integer(0);
		if (managedBean.getSmAppl().getSmCompanyBasic().getBumiPercentage() != null) {
			bumiPerc = managedBean.getSmAppl().getSmCompanyBasic()
					.getBumiPercentage().intValue();
			if (bumiPerc.compareTo(51) >= 0 && bumiPerc.compareTo(100) < 0) {
				bumiPerc = 99;
			}
		}
		//log.info("*** Bumi Percent Sent for Category L3 ***** " + bumiPerc);
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
		//log.info("*** Validate the code having active transaction ***** ");
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
					//log.info("**** Active Transactions ********"
							//+ resDTO.isHasActiveTransaction());
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
			//log.info("*** Has active transaction  *** "
					//+ hasActiveTransactionCategory);
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
		uomSelectItems = EntityHelper.convertListToSelectItem(uomList,
				"uomName", "uomId");

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
		raceSelectItems = EntityHelper.convertListToSelectItem(racesList,
				"raceName", "raceId");

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
		ReasonReqDTO reqDTO = new ReasonReqDTO();
		reqDTO.setReasonCategory(paramterCode);
		reqDTO.setLanguage(new FacesScopes().getUserLocale().getLanguage());
		ReasonResDTO resDTO = SmServiceLookup.getReasonSO()
				.retrieveReasonDescListByCategory(reqDTO);

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

	public static List<PmReasonDesc> retrieveMofRejectReason(String suppType,
			String bussType, String applType) {
		ReasonReqDTO reqDTO = new ReasonReqDTO();
		reqDTO.setLanguage(new FacesScopes().getUserLocale().getLanguage());
		reqDTO.setSuppType(suppType);
		reqDTO.setApplType(applType);
		reqDTO.setRegType(bussType);
		ReasonResDTO resDTO = SmServiceLookup.getReasonSO()
				.retrieveSupplierRejectionReason(reqDTO);
		return resDTO.getReasonDescList();
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
		if (!StringUtils.isBlank(applType)
				&& applType.equals(ParameterConst.NEW_SM_APPLN_FOR_BUMI)) {
			descList = SmServiceLookup.getStatusSO()
					.retrieveRegStatusListForBumi(reqDTO).getStatusDescList();
		} else {
			List<String> consultantRegStatus = Arrays
					.asList(new String[] {
							SupplierManagementConst.PM_STATUS_REGISTRATION_STATUS_REGISTERED
									+ "",
							SupplierManagementConst.PM_STATUS_REGISTRATION_STATUS_FOREIGN
									+ "" });
			descList = SmServiceLookup.getStatusSO()
					.retrieveRegStatusList(reqDTO).getStatusDescList();
			if (!EntityHelper.isCollectionEmpty(descList)
					&& !StringUtils.isBlank(suppType) && suppType.equals("J")) {
				List<PmStatusDesc> newList = new ArrayList<PmStatusDesc>();
				for (PmStatusDesc statDesc : descList) {
					if (statDesc.getPmStatus() != null
							&& statDesc.getPmStatus().getStatusId() != null
							&& (consultantRegStatus.contains(statDesc
									.getPmStatus().getStatusId().toString()))) {
						newList.add(statDesc);
					}
				}
				descList = newList;
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
			throw new EpException(MessageConst.ERROR_722568);
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
	//13320-Start
	public static PmDivision retrievePmDivsById(Long divnId) {
		IDivisionSO reqSO = (IDivisionSO) BusinessDelegate.getInstance()
				.getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
						IDivisionSO.class);
		DivisionReqDTO reqDTO = new DivisionReqDTO();
		reqDTO.setDivisionId(divnId);
		DivisionResDTO resDTO = reqSO.retrieveDivisionById(reqDTO);

		return resDTO.getPmDivision();
	}
	//13320-End
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
			log.error("**** THIS IS THE EXCEPTION IN RETRIVE LIST **** \n "+e.getMessage() );
			MofRegistrationResDTO resDTO = new MofRegistrationResDTO();
			resDTO.setErrorCaued(Boolean.TRUE);
			return resDTO;
		} finally {
			mofRegistrationSO = null;
		}

	}
	//#29823 - START
	/**
	 * Gets the last approved appliction for supplier.
	 * 
	 * @param managedBean
	 *            - SmApplicationManagedBean object
	 * @param res
	 * 			  - the PO or Approver response
	 * @return SmAppl object
	 */
	public static SmAppl getLastApprovedApplictionForSupplier(
			SmApplicationManagedBean managedBean, String res) {
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
			
			if (res.equalsIgnoreCase(my.ep.constant.sm.SupplierManagementConst.REJECT) && 
				managedBean.getBpmTaskName().equalsIgnoreCase(SupplierManagementConst.BPM_TASK_REVIEW_SITE_VISIT_AFTER_APPROVE) &&
				managedBean.getSmAppl().getSmSupplier() != null){
					reqDto.setSupplierId(managedBean.getSmAppl().getSmSupplier().getSupplierId());
					reqDto.setSmAppl(managedBean.getSmAppl());
					appl = revisionSO.retrieveLatestPreviousApproveAppl(reqDto).getSmAppl();
			}
			//#29823 - END
			//START 24964 && #28534
			if(appl == null && managedBean.getSmAppl().getSmSupplier() != null){
				reqDto.setSupplierId(managedBean.getSmAppl().getSmSupplier().getSupplierId());
				//#31043 - START
				reqDto.setSmAppl(managedBean.getSmAppl());
				//#31043 - END
				appl = revisionSO
						.retrieveLatestPreviousApproveAppl(reqDto).getSmAppl();
			}
			//END 24964 && #28534
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
		String s = setStringNavigationForApplTnCPage(applType);
		fc.getApplication().getNavigationHandler()
				.handleNavigation(fc, null, s);
	}
	
	public static String setStringNavigationForApplTnCPage(String applType){
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
		return s;
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
	public static SmAddress cloneAsPermenantAddr(SmAddress srcAddr,
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
		//START-13320
		if (srcAddr.getPmState().getStateCode().equalsIgnoreCase(SupplierManagementConst.SARAWAK_CODE)) {
			if (srcAddr.getDivisionId() != null) {
				desAddr.setPmDivision(SmCommonUtil.getInstance().getDivisionByUsingId(srcAddr.getDivisionId()));
				desAddr.setDivisionId(srcAddr.getDivisionId());
			}
		}
		//END-13320
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
		desAddr.setPostcode(srcAddr.getPostcode());
		//#27356
		desAddr.setMapLatitude(srcAddr.getMapLatitude());
		desAddr.setMapLongitude(srcAddr.getMapLongitude());
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
					srcAddr.getPmDistrict().getDistrictId()));
		}
		//#13320 - added division - start
		desAddr.setDivisionId(srcAddr.getDivisionId());
		if (srcAddr.getPmDivision() != null
				&& srcAddr.getPmDivision().getDivisionId() != null) {
			desAddr.setPmDivision(SmCommonUtil.getInstance().getDivisionByUsingId(
					srcAddr.getPmDivision().getDivisionId()));
		}
		//#13320 - added division - end
		desAddr.setDistrictId(srcAddr.getDistrictId());
		if (srcAddr.getPmCity() != null
				&& srcAddr.getPmCity().getCityId() != null) {
			desAddr.setPmCity(SmCommonUtil.getInstance().getCityById(
					srcAddr.getPmCity().getCityId()));
		}
		desAddr.setCityId(srcAddr.getCityId());
	}
	
	//#34277 - start
	/**
	 * Clone SSM address details
	 * @param desAddr target
	 * @param srcAddr source
	 */
	public static void cloneAddressSSM(SmAddress desAddr, SmAddress srcAddr){
		desAddr.setSsmData(srcAddr.isSsmData());
		desAddr.setAddressSsmNull(srcAddr.isAddressSsmNull());
		desAddr.setPostcodeSsmNull(srcAddr.isPostcodeSsmNull());
		desAddr.setStateSsmNull(srcAddr.isStateSsmNull());
		desAddr.setCountrySsmNull(srcAddr.isCountrySsmNull());
	}
	//#34277 - end

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
		//log.info("********* calculateShareHolderPercent ********  ");
		//log.info(" *** Non Bumi Equity **** "
				//+ managedBean.getSmAppl().getSmCompanyBasic()
						//.getNonBumiEquity());
		//log.info(" *** Bumi Equity **** "
				//+ managedBean.getSmAppl().getSmCompanyBasic().getBumiEquity());
		//log.info(" *** Non MY Equity **** "
				//+ managedBean.getSmAppl().getSmCompanyBasic()
						//.getForeignerEquity());

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
						new BigDecimal(0).setScale(3, RoundingMode.HALF_UP));
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
				if (managedBean.getSelectedShareHolder().getBumiFlgTrans() != null && managedBean.getSelectedShareHolder().getBumiFlgTrans() == 1) {
					resDto = perSO.retrieveBumiShareHoldersByApplId(reqDto);
					perType = SupplierManagementConst.PERSONNEL_TYPE_BUMI;
				} else {
					perType = SupplierManagementConst.PERSONNEL_TYPE_NON_BUMI;
					resDto = perSO.retrieveNonBumiShareHoldersByApplId(reqDto);
				}
			}
			if (resDto.getSmPersonnelList() != null) {
				selPerList = resDto.getSmPersonnelList();
				//log.info("*** List retrieved *** " + selPerList.size());
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
											.getPaidUpCapital(), 8,
											RoundingMode.HALF_UP).multiply(mul)
									.setScale(3, RoundingMode.HALF_UP));
				}

			} else {
				managedBean.getSelectedShareHolder().setSharePercentage(
						new BigDecimal(0).setScale(3, RoundingMode.HALF_UP));
				managedBean.getSelectedShareHolder().setShareAmt(
						new BigDecimal(0).setScale(3, RoundingMode.HALF_UP));
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
		if (!SmCommonUtil.isCollectionEmpty(list) && bankId != null
				&& branchId != null) {
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

		boolean valid = true;
		if (!EntityHelper.isCollectionEmpty(managedBean.getSmAppl()
				.getSmSupplierCategories())) {
			for (SmSupplierCategory cat : managedBean.getSmAppl()
					.getSmSupplierCategories()) {
				if (managedBean.isPoUser()
						&& (cat.getRecordStatusFlg() == SupplierManagementConst.PENDING_ACT_STATUS || cat
								.getRecordStatusFlg() == DbValueConst.RECORD_STATUS_PENDING_RE_APPROVAL)
						&& cat.getPoDecFlg() == null) {
					valid = false;
				} else if (managedBean.isApproverUser()
						&& (cat.getRecordStatusFlg() == SupplierManagementConst.PENDING_ACT_STATUS || cat
								.getRecordStatusFlg() == DbValueConst.RECORD_STATUS_PENDING_RE_APPROVAL)
						&& cat.getApprvrDecFlg() == null) {
					valid = false;
				}
			}
		}
		//log.info("*** isAllCatcodeVerified Valid For Approval After category checking Decision **** "
				//+ valid);
		if (valid) {
			valid = false;
			//#31043 start
			List<SmApplSupportDoc> licenseMandatoryDocs = new ArrayList<>();
			if (!EntityHelper.isCollectionEmpty(managedBean.getMandatoryLicenseDocs())) {
				for (SmLicenseInfoVO docVO : managedBean.getMandatoryLicenseDocs()) {
					licenseMandatoryDocs.addAll(docVO.getLicenseSuppDocs());
				}
			}
			if (SmApplicationUtil.isAllSuppDocAlreadyVerified(managedBean
					.getMandatoryDocs(), managedBean.getSmAppl()
					.getSupportingDocMode(), managedBean.isApproverUser())
					&& SmApplicationUtil.isAllSuppDocAlreadyVerified(
							managedBean.getNonMandatoryDocs(), managedBean
									.getSmAppl().getSupportingDocMode(),
							managedBean.isApproverUser())
							&& SmApplicationUtil.isAllSuppDocAlreadyVerified(
									licenseMandatoryDocs, managedBean
									.getSmAppl().getSupportingDocMode(),
							managedBean.isApproverUser())) {
				valid = true;
			}
		}
		if(managedBean.isPoUser() && !managedBean.getPoSecReview().isSitevisitVerified() && managedBean.getCurrentStatus() != null 
				&& (managedBean.getCurrentStatus().equalsIgnoreCase("Pending Review Site Visit Report - After Approval")
				|| managedBean.getCurrentStatus().equalsIgnoreCase("Pending Review Site Visit Report - Before Approval")
				|| managedBean.getCurrentStatus().equalsIgnoreCase("Menunggu Laporan Lawatan Tapak - Selepas Diluluskan")
				|| managedBean.getCurrentStatus().equalsIgnoreCase("Menunggu Laporan Lawatan Tapak - Sebelum Diluluskan"))){
			valid = false;
		}
		//#31043 end
		log.info("*** Valid For Approval Final Decision **** " + valid);
		managedBean.setAllowedToApprove(valid);
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

		valid = isValidSiteVisitSelection(valid,
				managedBean.getBumiSiteVisitVOList(), managedBean.getSmAppl()
						.getApplType());

		valid = isValidSiteVisitSelection(valid,
				managedBean.getDockYardSiteVisitVOList(), managedBean
						.getSmAppl().getApplType());
		valid = isValidSiteVisitSelection(valid,
				managedBean.getWorkshopSiteVisitVOList(), managedBean
						.getSmAppl().getApplType());
		valid = isValidSiteVisitSelection(valid,
				managedBean.getFactorySiteVisitVOList(), managedBean
						.getSmAppl().getApplType());
		valid = isValidSiteVisitSelection(valid,
				managedBean.getStoreSiteVisitVOList(), managedBean.getSmAppl()
						.getApplType());
		valid = isValidSiteVisitSelection(valid,
				managedBean.getBusinessOfficeSiteVisitVOList(), managedBean
						.getSmAppl().getApplType());

		return valid;

	}

	private static boolean isValidSiteVisitSelection(boolean valid,
			List<SmSiteVisitFacilityVO> siteVisitVOList, String applType) {
		if (!EntityHelper.isCollectionEmpty(siteVisitVOList) && valid) {
			for (SmSiteVisitFacilityVO facVO : siteVisitVOList) {
				if (!EntityHelper.isListGotSelectedValue(facVO
						.getAssocCompanies())
						&& !EntityHelper.isListGotSelectedValue(facVO
								.getSuppBranches())
						&& !EntityHelper.isListGotSelectedValue(facVO
								.getHeadQuarters())) {
					if (applType.equals(ParameterConst.NEW_SM_APPLN_FOR_BUMI)) {
						valid = false;
						throw new EpException(MessageConst.ERROR_722627,
								new String[] { StringUtils
										.lowerCase(SmCommonUtil.getMessage(
												"lbl.sm.site.visit", null)) });
					//#28775
					} else if (!EntityHelper.isListGotSelectedValue(facVO.getFacilities())) {
							valid = false;
							throw new EpException(MessageConst.ERROR_722627,
									new String[] { SmCommonUtil.getMessage(
											"lbl.sm.catCodes.catcode", null)
											+ " "
											+ facVO.getL3().getFullCatcode()
											+ " ("
											+ facVO.getL3().getCategoryName()
											+ ")" });
					//#28775
					}
				}
			}
		}
		return valid;
	}

	/**
	 * Validate if new special category added already assigned to facility for
	 * site visit purpose
	 * 
	 * @param SmApplicationManagedBean
	 *            bean
	 */
	public static void validateSpecialCategoryAssignedToFacility(
			SmApplicationManagedBean bean) {
		
		// #29232 (harith) @start
		MofRegistrationReqDTO reqDTO = new MofRegistrationReqDTO();
		reqDTO.setSupplierId(bean.getSmAppl().getSmSupplier().getSupplierId());
		// #29232 (harith) @end
		
		if (!EntityHelper.isCollectionEmpty(bean.getSmAppl()
				.getSmSupplierCategories()) && !bean.isApproverUser()) {
			SmSupplierCategoryFacility tmp = null;
			for (SmSupplierCategory cat : bean.getSmAppl()
					.getSmSupplierCategories()) {
				if (cat.isSpecialCategory()
						&& cat.getRecordStatusFlg() == DbValueConst.RECORD_STATUS_PENDING_ACTIVATION
						&& cat.getPmCategoryL3() != null
						&& cat.getPmCategoryL3().getFacilityType() != null
						&& !cat.getPmCategoryL3()
								.getFacilityType()
								.equals(SupplierManagementConst.SM_FACILITY_TYPE_BUSSINESSOFFICE)) {

					boolean exist = false;

					for (SmFacility fac : bean.getSmAppl().getSmFacilities()) {
						tmp = (SmSupplierCategoryFacility) EntityHelper
								.getEntityBySinglePropertyFromList(cat
										.getPmCategoryL3().getCategoryL3Id(),
										fac.getSmSupplierCategoryFacilities(),
										"pmCategoryL3");
						
						// #29232 (harith) @start
						if (tmp != null || !SmServiceLookup.getMofRegistrationSO().alreadyMof(reqDTO)
								.isValid()) {
							// #29232 (harith) @end
							exist = true;
							break;
						}
					}
					if (!exist) {
						throw new EpException(MessageConst.ERROR_722627,
								new String[] { SmCommonUtil.getMessage(
										"lbl.sm.catCodes.catcode", null)
										+ " "
										+ cat.getPmCategoryL3()
												.getFullCatcode()
										+ " ("
										+ cat.getPmCategoryL3()
												.getCategoryName() + ")" });
					}
				}
			}
		}
	}

	/**
	 * Construct Site visit data based on facilities and categories selected for
	 * site visit.
	 * @param bean
	 * @param voList
	 */
	public static void setSiteVisitData(SmApplicationManagedBean bean,
			List<SmSiteVisitFacilityVO> voList) {
		//28775 - Site Visit Grouping Enhancement - 28775 - start
		if (SmCommonUtil.isCollectionEmpty(bean.getSvTaskList())){
			bean.setSvTaskList(new ArrayList<SmSvTask>());
		}
		if (!SmCommonUtil.isCollectionEmpty(voList)) {
			//28775 - Site Visit Grouping Enhancement - 28775 - end

			// only set new list if svTask is empty
			if (EntityHelper.isCollectionEmpty(bean.getSmAppl().getSmSvTasks())) {
				bean.getSmAppl().setSmSvTasks(new ArrayList<SmSvTask>());
			} else {
				bean.setSvTaskIdsNeedToDelete(new ArrayList<Long>());
				for (SmSvTask svTask : bean.getSmAppl().getSmSvTasks()) {
					if (svTask.getSvTaskId() != null) {
						bean.getSvTaskIdsNeedToDelete().add(
								svTask.getSvTaskId());
					}
				}
			}

			SmSvTask svTask = null;
			PmParameter bumiType = SmCommonUtil.retrieveParameterByTypeAndCode(
					ParameterTypeConst.CATEGORY_TYPE,
					SupplierManagementConst.CAT_TYPE_BUMIPUTERA,
					new FacesScopes().getUserLocale().getLanguage());
			for (SmSiteVisitFacilityVO facVO : voList) {

				// add company basic address to site visit
				if (!SmCommonUtil.isCollectionEmpty(facVO.getHeadQuarters())) {
					for (SmCompanyAddress basic : facVO.getHeadQuarters()) {
						if (basic.isSelected()) {

							// set smSvTask related table
							setSmSvTask(
									bean,
									bumiType,
									facVO,
									svTask,
									basic.getSmAddress(),
									basic.getCompanyAddressId(),
									my.ep.constant.sm.SupplierManagementConst.SITE_ADDRESS_HQ);
						}
					}
				}

				// add assocCompany address to site visit
				if (!SmCommonUtil.isCollectionEmpty(facVO.getAssocCompanies())) {
					for (SmAssociateCompany assoc : facVO.getAssocCompanies()) {
						if (assoc.isSelected()) {

							// set smSvTask related table
							setSmSvTask(
									bean,
									bumiType,
									facVO,
									svTask,
									assoc.getSmAddress(),
									assoc.getAssociateCompanyId(),
									my.ep.constant.sm.SupplierManagementConst.SITE_ADDRESS_ASSOCIATE_COMPANY);
						}
					}
				}

				// add supplierBranch address to site visit
				if (!SmCommonUtil.isCollectionEmpty(facVO.getSuppBranches())) {
					for (SmSupplierBranch br : facVO.getSuppBranches()) {
						if (br.isSelected()) {

							// set smSvTask related table
							setSmSvTask(
									bean,
									bumiType,
									facVO,
									svTask,
									br.getSmAddress(),
									br.getSupplierBranchId(),
									my.ep.constant.sm.SupplierManagementConst.SITE_ADDRESS_BRANCH);
						}
					}
				}

				// add smFacility address to site visit
				if (!SmCommonUtil.isCollectionEmpty(facVO.getFacilities())) {
					for (SmFacility fac : facVO.getFacilities()) {
						if (fac.isSelected()) {

							// set smSvTask related table
							setSmSvTask(
									bean,
									bumiType,
									facVO,
									svTask,
									fac.getSmAddress(),
									fac.getFacilityId(),
									my.ep.constant.sm.SupplierManagementConst.SITE_ADDRESS_FACILITY);
						}
					}
				}
			}
		}
	}

	private static void setSmSvTask(SmApplicationManagedBean bean,
			PmParameter bumiType, SmSiteVisitFacilityVO facVO, SmSvTask svTask,
			SmAddress addr, Long siteRefId, String siteType) {
		
		//28775 - Site Visit Grouping Enhancement - 28775 - start
		if(!bean.getSmAppl().getApplType().equals(ParameterConst.NEW_SM_APPLN_FOR_BUMI)
				&& checkAddressAndParamSvTask(bean.getSvTaskList(), facVO, addr, siteType)){
			return;
		}
		//28775 - Site Visit Grouping Enhancement - 28775 - end
		
		// SmSvTask
		svTask = new SmSvTask();
		svTask.setSmAppl(bean.getSmAppl());
		if (!bean.getSmAppl().getApplType()
				.equals(ParameterConst.NEW_SM_APPLN_FOR_BUMI)) {
			svTask.setPmCategoryL3(facVO.getL3());
		}

		// SmSvOffice
		setSmSvOffice(bean, svTask);

		// SmSvTemplate
		setSmSvTemplate(bumiType, facVO.getL3(), svTask);

		// SmSvAddress
		setSmSvAddress(addr, svTask, siteRefId, siteType);

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

	private static void setSmSvAddress(SmAddress addr, SmSvTask svTask,
			Long siteRefId, String siteType) {
		SmSvAddress smSvAddress = new SmSvAddress();
		smSvAddress.setSmSvTask(svTask);
		smSvAddress.setSiteType(siteType);
		smSvAddress.setSiteRefId(siteRefId);
		//28775 - Site Visit Grouping Enhancement - 28775
		smSvAddress.setAddress(addr);
		svTask.setSmSvAddress(smSvAddress);
	}

	private static void setSmSvOffice(SmApplicationManagedBean bean,
			SmSvTask svTask) {
		SmSvOffice svOff = new SmSvOffice();
		svOff.setSmSvTask(svTask);
		svOff.setSmFacility(bean.getBasicCompanyFacility());
		svTask.setSmSvOffice(svOff);
	}
	
	//28775 - Site Visit Grouping Enhancement - 28775 - start
	/**
	 * Create SmSvTaskGroup if the address, facility type, and catcode type is the same with one of the SmSvTask(s)
	 * @param svTaskList
	 * @param facVO
	 * @param addr
	 * @param siteType
	 * @return true if SmSvTaskGroup has been created
	 */
	private static boolean checkAddressAndParamSvTask(List<SmSvTask> svTaskList, 
			SmSiteVisitFacilityVO facVO, SmAddress addr, String siteType){
		
		if(facVO.getL3() == null || facVO.getL3().getCategoryTypeCode() == null){
			return false;
			
		}
		String addr1 = AddressUtils.getSmFullFormattedAddress(addr);
		String param1 = facVO.getL3().getCategoryTypeCode().getParameterCode();
		
		for(SmSvTask task : svTaskList){
			String addr2 = AddressUtils.getSmFullFormattedAddress(task.getSmSvAddress().getAddress());
			String param2 = task.getSmSvTemplate().getPmParameter().getParameterCode();
			String siteType2 = task.getSmSvAddress().getSiteType();
			if(addr1.equals(addr2) && param1.equals(param2) && siteType.equals(siteType2)){
				addSmSvTaskGroup(task, facVO);
				return true;
			}
		}
		return false;
	}
	
	
	private static void addSmSvTaskGroup(SmSvTask svTask, SmSiteVisitFacilityVO facVO){
		SmSvTaskGroup group = new SmSvTaskGroup();
		group.setSmSvTask(svTask);
		SmSvTemplate template = new SmSvTemplate();
		template.setSmSvTaskGroup(group);
		group.setPmCategoryL3(facVO.getL3());
		template.setPmParameter(facVO.getL3().getCategoryTypeCode());
		group.setSmSvTemplate(template);
		
		if(svTask.getSmSvTaskGroups() == null){
			svTask.setSmSvTaskGroups(new ArrayList<SmSvTaskGroup>());
		}
		svTask.getSmSvTaskGroups().add(group);
	}
	//28775 - Site Visit Grouping Enhancement - 28775 - end

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

		// reset selected facilities in data table
		managedBean.setSelectedFactoryFacilities(new ArrayList<SmFacility>());
		managedBean.setSelectedDockyardFacilities(new ArrayList<SmFacility>());
		managedBean.setSelectedStoreFacilities(new ArrayList<SmFacility>());
		managedBean.setSelectedWorkShopFacilities(new ArrayList<SmFacility>());

		managedBean.setAddrSameAsItems(new ArrayList<SelectItem>());
		SelectItem item = new SelectItem();
		item.setValue(StringUtils.EMPTY);
		item.setLabel(SmCommonUtil.getMessage("lbl.txt.select", null));
		managedBean.getAddrSameAsItems().add(item);
		item = new SelectItem();
		item.setValue("HQ");
		item.setLabel(SmCommonUtil.getMessage("lbl.sm.facility.type.premise", null)); //#34711
		managedBean.getAddrSameAsItems().add(item);
		if (!EntityHelper.isCollectionEmpty(managedBean.getSmAppl()
				.getSmSupplierBranches())) {
			managedBean.getAddrSameAsItems().addAll(
					EntityHelper.convertListToSelectItem(managedBean
							.getSmAppl().getSmSupplierBranches(), "branchName",
							"supplierBranchId"));
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
							SupplierManagementConst.SM_FACILITY_TYPE_STORE)
							//#28775 - 38380
							|| facility.getFacilityType().equalsIgnoreCase(
								SupplierManagementConst.SM_FACILITY_TYPE_BUSSINESSOFFICE)) {
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
						&& (cat.getRecordStatusFlg().compareTo(
								SupplierManagementConst.PENDING_ACT_STATUS) == 0 || cat
								.getRecordStatusFlg()
								.compareTo(
										SupplierManagementConst.PENDING_RE_APPROVAL_STATUS) == 0)
						//#30766 & #34209 - START
						&& ( ((cat.getApprvrDecFlg()!=null && cat.getApprvrDecFlg() == 1) || (cat.getPoDecFlg()!=null && cat.getPoDecFlg()==1)) ||  (cat.getPoDecFlg()==null && cat.getApprvrDecFlg()==null))) { 
						//&& !isCategoryRejected(cat)) {
					   //#30766 & #34209 - END
					String facType = "";
					if (cat.getPmCategoryL3().getFacilityType() != null) {
						facType = cat.getPmCategoryL3().getFacilityType();
					}
					if (facType
							.equalsIgnoreCase(SupplierManagementConst.SM_FACILITY_TYPE_FACTORY)) {
						managedBean.setShowFactoryPanel(true);
					} else if (facType.equalsIgnoreCase(SupplierManagementConst.SM_FACILITY_TYPE_STORE)
							|| facType.equalsIgnoreCase(SupplierManagementConst.SM_FACILITY_TYPE_BUSSINESSOFFICE)) { //#30043
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

		managedBean.setDockYardFacilitiesVOList(SmApplicationUtil
				.getCategoryFacilityVO(managedBean.getSmAppl(),
						SupplierManagementConst.SM_FACILITY_TYPE_DOCKYARD));
		managedBean.setWorkshopFacilitiesVOList(SmApplicationUtil
				.getCategoryFacilityVO(managedBean.getSmAppl(),
						SupplierManagementConst.SM_FACILITY_TYPE_WORKSHOP));
		managedBean.setStoreFacilitiesVOList(SmApplicationUtil
				.getCategoryFacilityVO(managedBean.getSmAppl(),
						SupplierManagementConst.SM_FACILITY_TYPE_STORE));
		managedBean.setFactoryFacilitiesVOList(SmApplicationUtil
				.getCategoryFacilityVO(managedBean.getSmAppl(),
						SupplierManagementConst.SM_FACILITY_TYPE_FACTORY));
		
		// start #30043
		if (managedBean.isShowStorePanel()) {
			managedBean.getStoreFacilitiesVOList().addAll((SmApplicationUtil.getCategoryFacilityVO(managedBean.getSmAppl(),
					SupplierManagementConst.SM_FACILITY_TYPE_BUSSINESSOFFICE)));
			if (managedBean.getStoreFacilitiesVOList().isEmpty()) {
				managedBean.setShowStorePanel(false);
			}
		}
		// end #30043

		// set PO assign site visit popup VO value
		if (managedBean.isPoUser()
				&& managedBean.getCurrentPageType().equals(
						SupplierManagementConst.SM_APPL_PAGE_TYPE_PODEC)) {
			if (managedBean.getSmAppl().getApplType()
					.equals(ParameterConst.NEW_SM_APPLN_FOR_BUMI)) {
				SmFacility hq = getHqFacilityByAppl(managedBean.getSmAppl());
				SmSiteVisitFacilityVO facVO = new SmSiteVisitFacilityVO();
				facVO.setCatTypeLabel(SupplierManagementConst.FACILITY_TYPE_MAP
						.get(hq.getFacilityType()));
				facVO.setAssocCompanies(new ArrayList<SmAssociateCompany>());
				if (!EntityHelper.isCollectionEmpty(managedBean.getSmAppl()
						.getSmAssociateCompanies())) {
					for (SmAssociateCompany assoc : managedBean.getSmAppl()
							.getSmAssociateCompanies()) {
						facVO.getAssocCompanies().add(
								(SmAssociateCompany) SerializationUtils
										.clone(assoc));
					}
				}
				facVO.setSuppBranches(new ArrayList<SmSupplierBranch>());
				if (!EntityHelper.isCollectionEmpty(managedBean.getSmAppl()
						.getSmSupplierBranches())) {
					for (SmSupplierBranch br : managedBean.getSmAppl()
							.getSmSupplierBranches()) {
						facVO.getSuppBranches()
								.add((SmSupplierBranch) SerializationUtils
										.clone(br));
					}
				}
				facVO.setHeadQuarters(new ArrayList<SmCompanyAddress>());
				facVO.getHeadQuarters().add(
						(SmCompanyAddress) SerializationUtils.clone(managedBean
								.getSmAppl().getSmCompanyBasic()
								.getSmCompanyAddresses().get(0)));
				managedBean
						.setBumiSiteVisitVOList(new ArrayList<SmSiteVisitFacilityVO>());
				managedBean.getBumiSiteVisitVOList().add(facVO);
			} else {
				managedBean.setDockYardSiteVisitVOList(getSiteVisitFacilityVO(
						managedBean.getSmAppl(),
						managedBean.getDockYardFacilitiesVOList()));
				managedBean.setWorkshopSiteVisitVOList(getSiteVisitFacilityVO(
						managedBean.getSmAppl(),
						managedBean.getWorkshopFacilitiesVOList()));
				managedBean.setStoreSiteVisitVOList(getSiteVisitFacilityVO(
						managedBean.getSmAppl(),
						managedBean.getStoreFacilitiesVOList()));
				managedBean.setFactorySiteVisitVOList(getSiteVisitFacilityVO(
						managedBean.getSmAppl(),
						managedBean.getFactoryFacilitiesVOList()));
				managedBean
						.setBusinessOfficeSiteVisitVOList(getSiteVisitFacilityVO(
								managedBean.getSmAppl(),
								getBusinessOfficeSiteVisitVOList(managedBean)));
			}
		}
	}

	private static List<PmCategoryL3> getBusinessOfficeSiteVisitVOList(
			SmApplicationManagedBean bean) {
		//#28775 - start
		List<PmCategoryL3> businessOfficeSiteVisitList = new ArrayList<>();
		for (SmSupplierCategory cat : bean.getSmAppl().getSmSupplierCategories()) {
			if (!cat.isSpecialCategory() || cat.getPmCategoryL3() == null){
				continue;
			}
			boolean isValid = cat.getRecordStatusFlg() == DbValueConst.RECORD_STATUS_PENDING_ACTIVATION
					|| cat.getRecordStatusFlg() == DbValueConst.RECORD_STATUS_PENDING_RE_APPROVAL;
			
			if (SupplierManagementConst.SM_FACILITY_TYPE_BUSSINESSOFFICE.equals(cat.getPmCategoryL3().getFacilityType()) 
					&& isValid) {
				//#28775 - end
				businessOfficeSiteVisitList.add(cat.getPmCategoryL3());
			}
		}
		return businessOfficeSiteVisitList;
	}

	/**
	 * Show or hide travelling software section panel
	 * 
	 * @param managedBean
	 */
	public static void showOrHideTravelSoftwarePanel(
			SmApplicationManagedBean managedBean) {
		managedBean.setShowTravelSoftwarePanel(false);
		for (SmSupplierCategory cat : managedBean.getSmAppl()
				.getSmSupplierCategories()) {
			//26671 - Site Visit Grouping Enhancement -28775 - start
			boolean validStatus = isValidCategory(cat, managedBean.getSmAppl());
			if (cat.getPmCategoryL3().getCategoryTypeCode() != null	&& validStatus	&& !isCategoryRejected(cat)) {
				//26671 - Site Visit Grouping Enhancement -28775 - end
				String catType = cat.getPmCategoryL3().getCategoryTypeCode().getParameterCode();
				
				if (StringUtils.isNotBlank(catType)
						&& catType
								.equalsIgnoreCase(ParameterConst.CATEGORY_TYPE_TRAVEL)) {
					managedBean.setShowTravelSoftwarePanel(true);
				}
			}
		}
	}

	/**
	 * Initialize travelling software tick or untick checkbox
	 * 
	 * @param List
	 *            <PmMachine> mainTravellingSoftwareList
	 * @param List
	 *            <SmSupplierSystem> travellingSoftwareSystems
	 */
	public static void initializeTravellingSoftwareTicked(
			SmApplicationManagedBean bean) {
		bean.setSelectedAll(false);
		bean.setTravellingSoftwareList(SmCommonUtil.getInstance()
				.getPmMachineByCategoryTypeCode(
						ParameterConst.CATEGORY_TYPE_TRAVEL));

		// populate others travelling software in list
		List<PmMachine> addOthersMachineList = new ArrayList<PmMachine>();
		PmMachine others = null;
		if (!EntityHelper.isCollectionEmpty(bean.getSmAppl()
				.getSmSupplierSystems())) {
			for (SmSupplierSystem sys : bean.getSmAppl().getSmSupplierSystems()) {
				if (sys.getSystem() == null
						&& !StringUtils.isBlank(sys.getSystemName())) {
					others = new PmMachine();
					others.setMachineDesc(sys.getSystemName());
					addOthersMachineList.add(others);
				}
			}
		}

		// if others is empty, create one new machine object as default
		if (EntityHelper.isCollectionEmpty(addOthersMachineList)) {
			others = new PmMachine();
			addOthersMachineList.add(others);
		}

		if (!EntityHelper.isCollectionEmpty(bean.getTravellingSoftwareList())) {
			// add others travelling software to main machine list
			if (!EntityHelper.isCollectionEmpty(addOthersMachineList)) {
				bean.getTravellingSoftwareList().addAll(addOthersMachineList);
			}

			for (PmMachine machine : bean.getTravellingSoftwareList()) {
				if ((SmSupplierSystem) EntityHelper
						.getEntityBySinglePropertyFromList(machine
								.getMachineId(), bean.getSmAppl()
								.getSmSupplierSystems(), "system") != null) {
					machine.setSelected(true);
				} else {
					machine.setSelected(false);
				}
			}
		}
	}

	/**
	 * Update travelling software
	 * 
	 * @param SmApplicationManagedBean
	 *            bean
	 */
	public static void updateTravellingSoftware(SmApplicationManagedBean bean) {
		SmSupplierSystem travelSoftware = null;
		if (!EntityHelper.isCollectionEmpty(bean.getTravellingSoftwareList())) {
			List<SmSupplierSystem> selectedSystems = new ArrayList<SmSupplierSystem>();
			for (PmMachine mac : bean.getTravellingSoftwareList()) {
				if (mac.isSelected()
						|| (mac.getMachineId() == null && !StringUtils
								.isBlank(mac.getMachineDesc()))) {
					travelSoftware = getTicketingSystem(mac, bean.getSmAppl()
							.getSmSupplierSystems());
					if (travelSoftware == null) {
						travelSoftware = new SmSupplierSystem();
						if (mac.getMachineId() != null) {
							travelSoftware.setRevNo(1);
							travelSoftware.setSmAppl(bean.getSmAppl());
							travelSoftware.setSystem(mac);
							bean.getSmAppl().getSmSupplierSystems()
									.add(travelSoftware);
						} else {
							travelSoftware.setRevNo(1);
							travelSoftware.setSmAppl(bean.getSmAppl());
							travelSoftware.setSystemName(mac.getMachineDesc());
							bean.getSmAppl().getSmSupplierSystems()
									.add(travelSoftware);
						}
					} else {
						travelSoftware.setSystemName(mac.getMachineDesc());
					}
					selectedSystems.add(travelSoftware);
				}
			}

			// remove not selected system
			if (EntityHelper.isCollectionEmpty(selectedSystems)) {
				bean.getSmAppl().getSmSupplierSystems().clear();
			} else if (!EntityHelper.isCollectionEmpty(bean.getSmAppl()
					.getSmSupplierSystems())) {
				Iterator<SmSupplierSystem> removeIter = bean.getSmAppl()
						.getSmSupplierSystems().iterator();
				while (removeIter.hasNext()) {
					SmSupplierSystem removeObj = removeIter.next();
					if (getTicketingSystem(removeObj.getSystem(),
							selectedSystems) == null) {
						removeIter.remove();
					}
				}
			}
		}
	}

	private static SmSupplierSystem getTicketingSystem(PmMachine mac,
			List<SmSupplierSystem> systems) {
		SmSupplierSystem system = null;
		if (!EntityHelper.isCollectionEmpty(systems)) {
			if (mac != null && mac.getMachineId() != null) {
				SmSupplierSystem sys = (SmSupplierSystem) EntityHelper
						.getEntityBySinglePropertyFromList(mac.getMachineId(),
								systems, "system");
				if (sys != null) {
					system = sys;
				}
			} else {
				for (SmSupplierSystem sys : systems) {
					if (sys.getSystem() == null
							|| sys.getSystem().getMachineId() == null) {
						system = sys;
						break;
					}
				}
			}
		}
		return system;
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
			// removePendingApprovedCategoryIfNotValid(bean.getSmAppl(), bean
			// .getBasicCompanyFacility().getFacilityType());
			removeFacilitiesNotInTheSameCategoryFacType(bean.getSmAppl(), bean.getLatestAppl());
			removeTravellingSoftwareIfNotApplicable(bean);
			removeEquipmentIfNotApplicable(bean);
			removeOverlapCategoryIfDocNotUploaded(bean);
		}
	}

	/**
	 * Remove overlap category document if file name or file path is null (means
	 * not uploaded)
	 * 
	 * @param SmApplicationManagedBean
	 *            bean
	 */
	public void removeOverlapCategoryIfDocNotUploaded(
			SmApplicationManagedBean bean) {
		if (!EntityHelper.isCollectionEmpty(bean.getSmAppl()
				.getSmCategoryOverlapDocs())) {
			Iterator<SmCategoryOverlapDoc> it = bean.getSmAppl()
					.getSmCategoryOverlapDocs().iterator();
			while (it.hasNext()) {
				SmCategoryOverlapDoc itTmp = it.next();

				if (StringUtils.isBlank(itTmp.getFileName())
						|| StringUtils.isBlank(itTmp.getFilePath())) {
					it.remove();
				}
			}
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
			bean.setShowMachineryEquipmentPanel(false);

			if (!EntityHelper.isCollectionEmpty(bean.getSmAppl()
					.getSmMachineDtls())) {
				boolean canDelete = true;
				for (SmMachineDtl currMac : bean.getSmAppl().getSmMachineDtls()) {
					if (bean.getLatestAppl() != null
							&& isMachineExistInLatest(currMac, bean
									.getLatestAppl().getSmMachineDtls())) {
						canDelete = false;
						break;
					}
					//30508-start
					if (currMac !=null){
						canDelete = false;
						break;//30508-end
					}
				}
				if (canDelete) {
					appl.setSmMachineDtls(new ArrayList<SmMachineDtl>());
				}
			}

		} else {
			List<String> pendingApproveCatTypeList = new ArrayList<String>();
			if (appl.getApplCategoryType() != null && appl.getApplCategoryType().equals("N")) {
				//30508-start
				if (EntityHelper.isCollectionEmpty(appl.getSmSupplierCategories())) {
					appl.setSmManufacturingDtls(new ArrayList<SmManufacturingDtl>());
					appl.setSmMachineDtls(new ArrayList<SmMachineDtl>());
				}//30508-end
			}
			if (!EntityHelper.isCollectionEmpty(appl.getSmSupplierCategories())) {
				for (SmSupplierCategory cat : appl.getSmSupplierCategories()) {
					if ((cat.getRecordStatusFlg().compareTo(
							SupplierManagementConst.PENDING_ACT_STATUS) == 0 || cat
							.getRecordStatusFlg()
							.compareTo(
									SupplierManagementConst.PENDING_RE_APPROVAL_STATUS) == 0)
							//#30766 & #34209 - START
							//&& !isCategoryRejected(cat)
							) {
							//#30766 & #34209 - END
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
												.getParameterCode()))
							//#28677   //24964 & 28534 - start
								&& !appl.getApplType().equalsIgnoreCase(ParameterConst.NEW_SM_APPLN_FOR_MOF)) {
							if(!isMachineExistInLatest(mac, bean
									.getLatestAppl().getSmMachineDtls())){
								macIt.remove();
							} //#28677   //24964 & 28534 - end						
						}
					}
				}
			}
		}
	}

	private boolean isMachineExistInLatest(SmMachineDtl currMac,
			List<SmMachineDtl> latestMachines) {
		boolean retVal = false;

		if (!EntityHelper.isCollectionEmpty(latestMachines)) {
			if (currMac.getRecordStatusFlg() == DbValueConst.RECORD_STATUS_ACTIVE) {
				for (SmMachineDtl latestMac : latestMachines) {
					if (!EntityHelper.isObjectDifference(latestMac, currMac,
							null)) {
						retVal = true;
					}
				}
			}
		}
		return retVal;
	}

	/**
	 * Remove facility that dont have selected supplier category facility type
	 * 
	 * @param SmAppl
	 *            appl
	 */
	public void removeFacilitiesNotInTheSameCategoryFacType(SmAppl appl,
			SmAppl latestAppl) {
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
								.equals(SupplierManagementConst.SM_FACILITY_TYPE_BUSSINESSOFFICE)
						// #26384 - start
						&& (!appl.getApplType().equalsIgnoreCase(ParameterConst.NEW_SM_APPLN_FOR_MOF) && (!isFacilityExistInLatest(fac, latestAppl.getSmFacilities())))
						// #26384 - end
//						&& !isFacilityExistInLatest(fac, latestAppl.getSmFacilities())
						) {
					if (SmCommonUtil.isCollectionEmpty(catTypeList)
							|| !catTypeList.contains(fac.getFacilityType())) {
						facIt.remove();
					}
				}
			}
		}

	}
	
	//#34303 - Start
	/**
	 * Remove facility that have different facility type in sm_facility and pm_category_l3
	 * 
	 * @param SmAppl
	 *            appl
	 */
	public void removeFacilitiesHaveDifferentFacType(SmApplicationManagedBean bean) {
		if (!SmCommonUtil.isCollectionEmpty(bean.getSmAppl().getSmFacilities())) {
			for (Iterator<SmFacility> facIt = bean.getSmAppl().getSmFacilities().iterator(); facIt
					.hasNext();) {
				boolean deleteFacility = false;
				SmFacility fac = facIt.next();
				if (StringUtils.isNotBlank(fac.getFacilityType())
						&& !fac.getFacilityType()
								.equals(SupplierManagementConst.SM_FACILITY_TYPE_HOMEOFFICE)
						&& !fac.getFacilityType()
								.equals(SupplierManagementConst.SM_FACILITY_TYPE_BUSSINESSOFFICE)) {
					for (SmSupplierCategoryFacility cat : fac.getSmSupplierCategoryFacilities()) {
						if ((cat.getPmCategoryL3().getFacilityType()
								.equals(SupplierManagementConst.SM_FACILITY_TYPE_BUSSINESSOFFICE))
								&& (isfacilityValidToCompare(bean, cat))
								&& (!cat.getPmCategoryL3().getFacilityType()
										.equals(fac.getFacilityType()))) {
							deleteFacility = true;
						}
					}
					if(deleteFacility){
						facIt.remove();	
					}
				}
			}
		}
	}
	
	public boolean isfacilityValidToCompare(SmApplicationManagedBean bean, SmSupplierCategoryFacility cat){
		boolean retVal = false;
		for (SmSupplierCategory supCat : bean.getSmAppl().getSmSupplierCategories()) {
			if (StringUtils.isNotBlank(supCat.getPmCategoryL3()
					.getFacilityType())) {
				if ((supCat.getRecordStatusFlg().compareTo(
						SupplierManagementConst.PENDING_ACT_STATUS) == 0 || supCat
						.getRecordStatusFlg()
						.compareTo(
								SupplierManagementConst.PENDING_RE_APPROVAL_STATUS) == 0)
						&& !isCategoryRejected(supCat)) {
					if(supCat.getPmCategoryL3().getCategoryL3Id().equals(cat.getPmCategoryL3().getCategoryL3Id())){
						retVal = true;
					}
				}
			}
		}
		return retVal;
	}
	//#34303 - End

	private boolean isFacilityExistInLatest(SmFacility fac,
			List<SmFacility> latestFacilities) {
		boolean retVal = false;

		if (!EntityHelper.isCollectionEmpty(latestFacilities)) {
			if (fac.getRecordStatusFlg() == DbValueConst.RECORD_STATUS_ACTIVE) {
				for (SmFacility latestFac : latestFacilities) {
					if (!(latestFac
							.getFacilityType()
							.equals(SupplierManagementConst.SM_FACILITY_TYPE_HOMEOFFICE) || latestFac
							.getFacilityType()
							.equals(SupplierManagementConst.SM_FACILITY_TYPE_BUSSINESSOFFICE))
							&& !EntityHelper.isObjectDifference(latestFac, fac,
									null)) {
						retVal = true;
					}
				}
			}
		}
		return retVal;
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

	public static boolean isCategoryRejected(SmSupplierCategory cat) {
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
	 * @param string
	 *            - role code
	 */
	// #29664 & #29663- start
	public static void manageAndSaveNewSmRemarksWithRoleCode(Long docId, String docType, String remarks, String roleCode) {
		//System.out.println("manageAndSaveNewSmRemarksWithRoleCode | roleCode = " + roleCode);
		SmRemark remark = new SmRemark();
		remark.setDocId(docId);
		remark.setDocType(docType);
		remark.setRemark(remarks);
		if (roleCode != null) {
			PmRole role = new PmRole();
			role.setRoleCode(roleCode);
			remark.setPmRole(role);
		}
		saveSmRemarks(remark);
	}
	// #29664 & #29663 - end

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
	 * Remove personnel if admin or softcert is processing from param list
	 * 
	 * @param List
	 *            <SmPersonnel> personnelList
	 */
	public static void removePersonnelAdminOrSoftcertProcessFromList(
			List<SmPersonnel> personnelList) {
		if (personnelList != null) {
			Iterator<SmPersonnel> iterator = personnelList.iterator();
			while (iterator.hasNext()) {
				SmPersonnel personnel = iterator.next();
				if ((personnel.getEpRole() != null && (personnel.getEpRole()
						.equals(RoleCode.SUPPLIER_TEMP.value())
						|| personnel.getEpRole().equalsIgnoreCase(
								RoleCode.MOF_SUPPLIER_ADMIN.value()) || personnel
						.getEpRole().equalsIgnoreCase(
								RoleCode.BASIC_SUPPLIER_ADMIN.value())))
						|| personnel.getSoftcertFlg() == SupplierManagementConst.SOFT_CERT_PROCESSING || personnel.isSsmData()) {
					iterator.remove();
				}
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
					managedBean.isReviewSiteVisit(),
					managedBean.isShowTravelSoftwarePanel(),
					managedBean.isShowMachineryEquipmentPanel(),
					managedBean.isShowFactoryPanel(),
					managedBean.isShowDockyardPanel(),
					managedBean.isShowStorePanel(),
					managedBean.isShowWorkShopPanel());
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
			log.error("Error in isLatestApprovedPersonnelAlreadyAuthorized:"
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
				} else {
					managedBean.setBasicCompanyStates(stateLs);
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
			long dayBal = 0;
			if (smAppl.getDraftExpiryDate() != null) {
				dayBal = DateUtils.getDaysDifference(new Date(),
						smAppl.getDraftExpiryDate()) + 1;
			}
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

		if (SupplierManagementConst.LIST_OF_BUSINESS_TYPE_NOT_ALLOWED_TO_EDIT_COMPANY_NAME
				.contains(smSupplier.getBusinessType())) {
			retVal = true;
		} else if (isApplHasSoftcertProcessingNotComplete(smAppl)) {
			retVal = true;
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
		}

		// disable due to ticket #17505
		// else if (isApplHasSoftcertProcessingNotComplete(smAppl)) {
		// retVal = true;
		// }
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
		reqDTO.setNotNeedInitialize(true);
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
						//#33129 - START 
						//maintain softcert 1 if the personnel was an admin previously
						if(currPersonnel.getSoftcertFlg() != SupplierManagementConst.SOFT_CERT_ISSUED){
							currPersonnel.setSoftcertFlg(SupplierManagementConst.SOFT_CERT_PENDING_REQ);
							currPersonnel.setContractSignerFlg(0);
						}
						//#33129 - END
					
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
			if (currPersonnel.getEpRole() != null
					&& currPersonnel.getEpRole().equals(
							RoleCode.CM_USER.value())) {
				currPersonnel.setSoftcertFlg(0);
				currPersonnel.setContractSignerFlg(0);
			} else {
				currPersonnel.setSoftcertFlg(StringUtils.isBlank(currPersonnel
						.getEpRole()) ? 0
						: SupplierManagementConst.SOFT_CERT_PENDING_REQ);
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
	 * Get latest rejected application by supplier ID and application type
	 * 
	 * @param SmAppl
	 *            rejectedAppl
	 * @return SmAppl
	 */
	public static SmAppl getLatesRejectedApplicationBySuppIdAndAppType(
			Long supplierId, String applType) {
		SmAppl smAppl = null;
		StringBuilder sb = new StringBuilder();
		sb.append("(")
				.append(SupplierManagementConst.PM_STATUS_REJECTED_BY_PROCESSING_OFFICER)
				.append(",")
				.append(SupplierManagementConst.PM_STATUS_REJECTED_BY_APPROVER)
				.append(",")
				.append(SupplierManagementConst.PM_STATUS_REJECTED_BY_SYSTEM)
				//#32627 START
				.append(",")
				.append(SupplierManagementConst.PM_STATUS_REJECTED_BY_SYSTEM_SDO_INQUIRY)
				.append(",")
				.append(SupplierManagementConst.PM_STATUS_REJECTED_BY_SYSTEM_PO_INQUIRY)
				.append(",")
				.append(SupplierManagementConst.PM_STATUS_AUTOREJECT_UNSUBMIT_OFFLINE_DOCUMENT)
				//#32627 END
				.append(")");
		MofRegistrationReqDTO reqDTO = new MofRegistrationReqDTO();
		reqDTO.setSupplierId(supplierId);
		reqDTO.setWorkflowStatus(sb.toString());
		reqDTO.setAppType(applType);
		Long applId = SmServiceLookup.getMofRegistrationSO()
				.retriveApplIdForLastRejectedApplicationByApplType(reqDTO)
				.getApplId();
		if (applId != null) {
			reqDTO = new MofRegistrationReqDTO();
			reqDTO.setApplId(applId);
			smAppl = SmServiceLookup.getMofRegistrationSO()
					.retrieveSmApplicationDetailsById(reqDTO).getSmAppl();
		}
		return smAppl;
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
				if (gstCurrent.getApplNo().equals(appl.getApplNo())
						|| (!appl.getApplType().equals(
								ParameterConst.NEW_SM_APPLN_FOR_MOF) && !appl
								.getApplType().equals(
										ParameterConst.NEW_SM_APPLN_FOR_RENEW))) {
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
				if ((appl.getApplType().equals(
						ParameterConst.NEW_SM_APPLN_FOR_MOF) && appl
						.getApplType().equals(
								ParameterConst.NEW_SM_APPLN_FOR_RENEW))) {
					gst = new SmGstSupplier();
				}
			}
		}
		if ((appl.getApplType().equals(ParameterConst.NEW_SM_APPLN_FOR_MOF) || appl
				.getApplType().equals(ParameterConst.NEW_SM_APPLN_FOR_RENEW))) {
			initGstSuppDocs(gst);
		}
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
									doc.setSmGstSupplier(br.getGst());
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

	// harris
	public static void initBranchSstInformation(SmSstSupplier hqSst, SmAppl appl) {
		SupplierSstReqDTO reqDTO = null;
		SmSstSupplier brCurr = null;
		SmSstSupplier brByApplNo = null;
		List<SmSupplierBranch> branchList = appl.getSmSupplierBranches();
		if (!isSstEmpty(hqSst) && branchList != null) {
			for (SmSupplierBranch br : branchList) {
				reqDTO = new SupplierSstReqDTO();
				reqDTO.setApplNo(appl.getApplNo());
				reqDTO.setBranchCode(br.getBranchCode() == null ? br
						.getSupplierBranchId() : br.getBranchCode());
				brByApplNo = SmServiceLookup.getSupplierSstSO()
						.retrieveSstInfoByApplNoAndBranchCode(reqDTO)
						.getSmSstSupplier();
				brCurr = SmCommonUtil
						.getSmSstSupplierBySupplierIdAndBranchCode(appl
								.getSmSupplier().getSupplierId(), br
								.getBranchCode() != null ? br.getBranchCode()
								: br.getSupplierBranchId(), null, true);

				if (brByApplNo != null) {
					br.setSst(brByApplNo);
					br.setSameAsHq(SmApplicationUtil.isBranchSstSameAsHq(hqSst,
							br.getSst()));
				} else {
					if (brCurr != null) {
						if (brCurr.getApplNo().equals(appl.getApplNo())) {
							br.setSst(brCurr);
							br.setSameAsHq(SmApplicationUtil
									.isBranchSstSameAsHq(hqSst, br.getSst()));
						} else {
							br.setSst(new SmSstSupplier());
							br.getSst().setSalestaxregno(
									brCurr.getSalestaxregno());
							br.getSst().setSalestaxregDate(
									brCurr.getSalestaxregDate());
							br.getSst().setSalestaxregEndDate(
									brCurr.getSalestaxregEndDate());
							br.getSst().setServtaxregno(
									brCurr.getServtaxregno());
							br.getSst().setServtaxregDate(
									brCurr.getServtaxregDate());
							br.getSst().setServtaxregEndDate(
									brCurr.getServtaxregEndDate());
							br.setSameAsHq(SmApplicationUtil
									.isBranchSstSameAsHq(hqSst, br.getSst()));
							// if (brCurr.getSmGstSupplierDocs() != null
							// && !brCurr.getSmGstSupplierDocs().isEmpty()) {
							// br.getGst().setSmGstSupplierDocs(
							// new ArrayList<SmGstSupplierDoc>());
							// SmGstSupplierDoc doc = null;
							// for (SmGstSupplierDoc docCurr : brCurr
							// .getSmGstSupplierDocs()) {
							// doc = new SmGstSupplierDoc();
							// doc.setPmDocument(docCurr.getPmDocument());
							// doc.setFileName(docCurr.getFileName());
							// doc.setFilePath(docCurr.getFilePath());
							// doc.setSmGstSupplier(br.getGst());
							// br.getGst().getSmGstSupplierDocs().add(doc);
							// }
							// }
						}
					} else {
						br.setSst(new SmSstSupplier());
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
	public static int getBranchPaymentBankCount(SmSupplierBranch br) {
		int paymntBankCount = 0;
		MofRegistrationReqDTO reqDTO = new MofRegistrationReqDTO();
		reqDTO.setBranchId(br.getSupplierBranchId());
		paymntBankCount = SmServiceLookup.getMofRegistrationSO()
				.countBranchPaymentBank(reqDTO).getCount();
		return paymntBankCount;
	}

	/**
	 * Validate if added associate to HQ already reach maximum bank account for
	 * payment purpose
	 * 
	 * @param SmAppl
	 *            smAppl
	 * @param SmSupplierBank
	 *            selectedBank
	 */
	public static void validateBankLimitForHq(SmAppl smAppl,
			SmSupplierBank selectedBank) {
		SmAppl cloneAppl = (SmAppl) SerializationUtils.clone(smAppl);
		EntityHelper.addOrUpdateEntityToList(selectedBank,
				cloneAppl.getSmSupplierBanks());
		int maxPaymentBank = SmServiceLookup.getSmRuleSO()
				.retrieveMaxPaymentBank().getMaxPaymentBank();
		int paymentBankCount = 0;

		for (SmSupplierBank bank : cloneAppl.getSmSupplierBanks()) {
			if (bank.getForHqFlg() == DbValueConst.RECORD_STATUS_ACTIVE
					&& bank.getAccountPurpose() != null
					&& (bank.getAccountPurpose().equals("B") || bank
							.getAccountPurpose().equals("P"))) {
				paymentBankCount++;
			}
		}

		if (paymentBankCount > maxPaymentBank) {
			throw new EpException(MessageConst.ERROR_721049,
					new String[] { String.valueOf(maxPaymentBank) });
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

	// harris
	public static boolean isSstEmpty(SmSstSupplier sst) {
		boolean retVal = false;
		if (sst == null) {
			retVal = true;
		} else if (StringUtils.isBlank(sst.getSalestaxregno())
				&& sst.getSalestaxregDate() == null
				&& sst.getSalestaxregEndDate() == null) {
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
			if (!EntityHelper.isCollectionEmpty(gst.getSmGstSupplierDocs())) {
				// sort by doc name
				Collections.sort(gst.getSmGstSupplierDocs(),
						new Comparator<SmGstSupplierDoc>() {
							@Override
							public int compare(final SmGstSupplierDoc s1,
									final SmGstSupplierDoc s2) {
								String str1 = s1.getPmDocument() == null ? StringUtils.EMPTY
										: s1.getPmDocument().getDocName();
								String str2 = s2.getPmDocument() == null ? StringUtils.EMPTY
										: s2.getPmDocument().getDocName();
								return (str1.compareTo(str2));
							}
						});
			}
		}
	}

	/**
	 * Initialize overlap category supporting documents
	 * 
	 * @param SmAppl
	 *            smAppl
	 */
	public static void initOverlapCategoryDocs(boolean hasOverlapCategory,
			SmApplicationManagedBean bean) {
		if (hasOverlapCategory) {
			SmAppl smAppl = bean.getSmAppl();
			DocumentReqDTO reqDTO = new DocumentReqDTO();
			reqDTO.setUserLocale(new FacesScopes().getUserLocale());
			List<PmDocument> suppDocList = SmServiceLookup.getDocumentSO()
					.retrieveOverlapCategorySuppDocuments(reqDTO)
					.getDocumentList();
			SmCategoryOverlapDoc suppDoc = null;

			if (!EntityHelper.isCollectionEmpty(suppDocList)) {
				if (EntityHelper.isCollectionEmpty(bean.getSmAppl()
						.getSmCategoryOverlapDocs())) {
					bean.setOverlapDocs(new ArrayList<SmCategoryOverlapDoc>());
					for (PmDocument doc : suppDocList) {
						suppDoc = new SmCategoryOverlapDoc();
						suppDoc.setPmDocument(doc);
						suppDoc.setSmAppl(smAppl);
						bean.getOverlapDocs().add(suppDoc);
					}
				} else {
					bean.setOverlapDocs(bean.getSmAppl()
							.getSmCategoryOverlapDocs());
					for (PmDocument doc : suppDocList) {
						if (EntityHelper.getEntityBySinglePropertyFromList(
								doc.getDocumentId(), bean.getOverlapDocs(),
								"pmDocument") == null) {
							suppDoc = new SmCategoryOverlapDoc();
							suppDoc.setPmDocument(doc);
							suppDoc.setSmAppl(smAppl);
							bean.getOverlapDocs().add(suppDoc);
						}
					}
				}
			}
		} else {
			bean.getSmAppl().setSmCategoryOverlapDocs(
					new ArrayList<SmCategoryOverlapDoc>());
			bean.setOverlapDocs(new ArrayList<SmCategoryOverlapDoc>());
			bean.setOverLappedCategoryExists(false); //#31018
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

	public static void validateRenewalApplTypeForSpecialCategory(SmAppl smAppl) {
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

	// harris
	public static void setUpdatedBranchSstToMainList(
			List<SmSupplierBranch> branchList, SmSstSupplier sst) {
		if (branchList != null && !branchList.isEmpty()) {
			for (SmSupplierBranch br : branchList) {
				if (br.getBranchCode() != null
						&& sst.getBranchCode() != null
						&& br.getBranchCode().compareTo(sst.getBranchCode()) == 0) {
					br.setSst(sst);
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

	// harris
	public static boolean isBranchSstSameAsHq(SmSstSupplier hqSst,
			SmSstSupplier brSst) {
		boolean retVal = false;
		if (hqSst.getSalestaxregDate() != null
				&& brSst.getSalestaxregDate() != null
				&& StringUtils.isNotBlank(brSst.getSalestaxregno())
				&& StringUtils.isNotBlank(hqSst.getSalestaxregno())
				&& brSst.getSalestaxregno().equals(hqSst.getSalestaxregno())
				&& brSst.getSalestaxregDate().compareTo(
						hqSst.getSalestaxregDate()) == 0) {
			retVal = true;
		}
		return retVal;
	}

	public static SmFacility getHqFacilityByAppl(SmAppl appl) {
		// #34303 - start
		for (SmFacility smFac : appl.getSmFacilities()) {
			if ((smFac.getFacilityType().equalsIgnoreCase(
					SupplierManagementConst.SM_FACILITY_TYPE_HOMEOFFICE) || smFac
					.getFacilityType()
					.equalsIgnoreCase(
							SupplierManagementConst.SM_FACILITY_TYPE_BUSSINESSOFFICE))
					&& smFac.getRecordStatusFlg() != DbValueConst.RECORD_STATUS_DELETED
					&& smFac.isHeadquarters()) {
				return smFac;
			}
		}

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
		// #34303 - end
		return null;
	}
	
	// #34303 - start
	public static void setFacilityTypeNotInTheSameCategoryFacType(
			SmApplicationManagedBean smApplicationManagedBean) {
		for (SmFacility facility : smApplicationManagedBean.getSmAppl()
				.getSmFacilities()) {
			if (facility.getFacilityId() != null) {
				for (SmSupplierCategoryFacility fac : facility
						.getSmSupplierCategoryFacilities()) {
					if (facility.getFacilityId() != null) {
						if (facility.getFacilityType().equalsIgnoreCase(
								SupplierManagementConst.SM_FACILITY_TYPE_STORE)
								&& fac.getPmCategoryL3()
										.getFacilityType()
										.equalsIgnoreCase(
												SupplierManagementConst.SM_FACILITY_TYPE_BUSSINESSOFFICE)) {
							facility.setFacilityType(SupplierManagementConst.SM_FACILITY_TYPE_BUSSINESSOFFICE);
						}
					}
				}
			}
		}
	}
	// #34303 - end

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
				//log.info("**** This is an unknown panel ***** ");
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
			bean.setDivisionBox(true);
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
				//log.info("**** This is an unknown panel ***** ");
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
				//log.info("**** This is an unknown panel ***** ");
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
		//13320-Address
		PmState state = (SmCommonUtil.getInstance().getStateById(addr.getPmState().getStateId()));
		if(state != null){
		if(state.getStateCode().equalsIgnoreCase(SupplierManagementConst.SARAWAK_CODE))
		getInstance().populateDivisionsByState(addr, panelName, bean);
		if (addr.getPmDivision()!= null) {
		addr.setDivisionId(addr.getPmDivision().getDivisionId());
			}
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
			if(addr.getPmState().getStateCode().equalsIgnoreCase(SupplierManagementConst.SARAWAK_CODE)){
			if (addr.getPmDivision()!= null
					&& addr.getPmDivision().getDivisionId() != null) {
				addr.setDivisionId(addr.getPmDivision().getDivisionId());//13320-For Address
			}
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
			if(addr.getPmState().getStateCode().equalsIgnoreCase(SupplierManagementConst.SARAWAK_CODE)){
			if (addr.getDivisionId() != null) {
				addr.setPmDivision(SmCommonUtil.getInstance().getDivisionByUsingId(
						addr.getDivisionId()));//13320-For Address
			}
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

	private boolean isCatL3Valid(SmAppl appl, String facilityType,
			SmSupplierCategory cat) {
		boolean retVal = true;
		String suppType = StringUtils.isBlank(appl.getSupplierType()) ? ""
				: appl.getSupplierType();
		BigDecimal paidUpCapital = (appl.getSmCompanyBasic() != null && appl
				.getSmCompanyBasic().getPaidUpCapital() != null) ? appl
				.getSmCompanyBasic().getPaidUpCapital() : BigDecimal.ZERO;
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
				reqDTO.setPaidCapital(paidUpCapital);
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
		List<SelectItem> categoryTypeItems = new ArrayList<>();
		if (!EntityHelper.isCollectionEmpty(appl.getSmSupplierCategories())) {
			for (SmSupplierCategory cat : appl.getSmSupplierCategories()) {
				//26671 - Site Visit Grouping Enhancement -28775, //#32008 - after approval bug
				boolean validStatus = isValidCategory(cat, appl);
				validStatus &= !isCategoryRejected(cat);
				if (validStatus && cat.getPmCategoryL3() != null
						&& cat.getPmCategoryL3().getCategoryTypeCode() != null
						&& Arrays
								.asList(my.ep.constant.sm.SupplierManagementConst.SM_CAT_TYPE_MACHINERY)
								.contains(
										cat.getPmCategoryL3()
												.getCategoryTypeCode()
												.getParameterCode())) {
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
	 * @param categoryTypeId
	 * @param appl
	 * @return List<SelectItem>
	 */
	public static List<SelectItem> getCategoryCodesItemsByCatTypeId(
			Long categoryTypeId, SmAppl appl) {
		//28778 - Site Visit Grouping Enhancement -28775 - start
		List<SelectItem> categoryCodeItems = new ArrayList<>();
		List<Integer> validStatus = Arrays.asList(
				DbValueConst.RECORD_STATUS_PENDING_RE_APPROVAL, DbValueConst.RECORD_STATUS_PENDING_ACTIVATION);
		if (!EntityHelper.isCollectionEmpty(appl.getSmSupplierCategories())) {
			for (SmSupplierCategory cat : appl.getSmSupplierCategories()) {
				if (validStatus.contains(cat.getRecordStatusFlg())		//28778 - end
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
//					categoryTypeDesc = desc != null ? desc.getCodeName()
//							: categoryTypeDesc;

					if (bean.isShowMachineryEquipmentPanel()) {
						if (bean.isShowFactoryPanel()) {
							// SmManufacturingDtl
							if (EntityHelper.isCollectionEmpty(appl.getSmManufacturingDtls())) {
								categoryTypeDesc = desc != null ? SmCommonUtil.getMessage("lbl.sm.manufacture.panHdr", null) : categoryTypeDesc;
								valid = false;
								if (currentTab != -1) {
									setNavigationBackToCurrentPage(currentTab, bean);
								}
								throw new EpException(MessageConst.ERROR_722631, new String[] { cat.getPmCategoryL3().getFullCatcode(), categoryTypeDesc });
							}
						}
						
						// SmMachineDtl
						if (EntityHelper.isCollectionEmpty(appl
								.getSmMachineDtls())) {
							categoryTypeDesc = desc != null ? SmCommonUtil.getMessage("lbl.sm.machine.panHdr", null) : categoryTypeDesc;
							valid = false;
							if (currentTab != -1) {
								setNavigationBackToCurrentPage(currentTab, bean);
							}
							throw new EpException(MessageConst.ERROR_722631,
									new String[] {
											cat.getPmCategoryL3()
													.getFullCatcode(),
											categoryTypeDesc });
						} else {
							if ((SmMachineDtl) EntityHelper
									.getEntityBySinglePropertyFromList(cat
											.getPmCategoryL3()
											.getCategoryTypeCode()
											.getParameterId(),
											appl.getSmMachineDtls(),
											"categoryType") == null) {
								valid = false;
								if (currentTab != -1) {
									setNavigationBackToCurrentPage(currentTab,
											bean);
								}
								throw new EpException(
										MessageConst.ERROR_722631,
										new String[] {
												cat.getPmCategoryL3()
														.getFullCatcode(),
												categoryTypeDesc });

							} else {
								List<SmMachineCategory> machineCategories = new ArrayList<SmMachineCategory>();
								for (SmMachineDtl mac : appl.getSmMachineDtls()) {
									//Bug #26616
									if (mac.getCategoryType() != null) {
										if (mac.getCategoryType()
												.getParameterId()
												.compareTo(
														cat.getPmCategoryL3()
																.getCategoryTypeCode()
																.getParameterId()) == 0) {
											machineCategories.addAll(mac
													.getSmMachineCategories());

										}
									}
								}

								SmMachineCategory macCat = (SmMachineCategory) EntityHelper
										.getEntityBySinglePropertyFromList(cat
												.getPmCategoryL3()
												.getCategoryL3Id(),
												machineCategories,
												"pmCategoryL3");

								if (macCat == null) {
									valid = false;
									if (currentTab != -1) {
										setNavigationBackToCurrentPage(
												currentTab, bean);
									}
									throw new EpException(
											MessageConst.ERROR_722631,
											new String[] {
													cat.getPmCategoryL3()
															.getFullCatcode(),
													categoryTypeDesc });
								}

							}
						}
					}

				}
			}
		}

		validTravellingSoftware(valid, currentTab, bean);

		return valid;
	}

	private static boolean validTravellingSoftware(boolean valid,
			int currentTab, SmApplicationManagedBean bean) {
		if (bean.isShowTravelSoftwarePanel()
				&& !EntityHelper.isCollectionEmpty(bean
						.getTravellingSoftwareList())) {
			valid = false;
			for (PmMachine mac : bean.getTravellingSoftwareList()) {
				if (mac.getMachineId() != null) {
					if (mac.isSelected()) {
						valid = true;
						break;
					}
				} else {
					if (!StringUtils.isBlank(mac.getMachineDesc())) {
						valid = true;
						break;
					}
				}
			}
			if (!valid) {
				if (currentTab != -1) {
					setNavigationBackToCurrentPage(currentTab, bean);
				}
				for (SmSupplierCategory cat : bean.getSmAppl()
						.getSmSupplierCategories()) {
					if (cat.isSpecialCategory()
							&& cat.getPmCategoryL3() != null
							&& cat.getPmCategoryL3().getCategoryTypeCode() != null
							&& cat.getPmCategoryL3().getCategoryTypeCode()
									.getParameterId() != null
							&& cat.getRecordStatusFlg() != null
							&& cat.getRecordStatusFlg().compareTo(
									SupplierManagementConst.PENDING_ACT_STATUS) == 0
							&& !SmApplicationUtil.isCategoryRejected(cat)) {
						String catType = "";
						if (cat.getPmCategoryL3().getCategoryTypeCode() != null) {
							catType = cat.getPmCategoryL3()
									.getCategoryTypeCode().getParameterCode();
						}
						if (StringUtils.isNotBlank(catType)
								&& catType
										.equalsIgnoreCase(ParameterConst.CATEGORY_TYPE_TRAVEL)) {
							throw new EpException(MessageConst.ERROR_722644,
									new String[] { cat.getPmCategoryL3()
											.getCategoryName()
											+ " - "
											+ cat.getPmCategoryL3()
													.getFullCatcode() });
						}
					}
				}
			}
		}
		return valid;
	}

	public static String getMachineDeleteErrMsgKey(SmAppl appl,
			List<SmMachineDtl> machToDeleteList,
			boolean isShowMachineryEquipmentPanel) {
		String retVal = StringUtils.EMPTY;
		String categoryTypeDesc = StringUtils.EMPTY;

		// remove selected from main list
		Iterator<SmMachineDtl> mainIter = appl.getSmMachineDtls().iterator();
		while (mainIter.hasNext()) {
			SmMachineDtl mainObj = mainIter.next();
			for (SmMachineDtl toRemoveObj : machToDeleteList) {
				if (mainObj.getMachineDtlId().compareTo(
						toRemoveObj.getMachineDtlId()) == 0) {
					mainIter.remove();
				}
			}
		}

		// check at least one category is needed and cant be deleted
		List<SmMachineCategory> macCategoryList = new ArrayList<SmMachineCategory>();
		for (SmMachineDtl machDtl : appl.getSmMachineDtls()) {
			macCategoryList.addAll(machDtl.getSmMachineCategories());
		}

		for (SmMachineDtl toRemoveObj : machToDeleteList) {
			for (SmMachineCategory toRemoveMachCat : toRemoveObj
					.getSmMachineCategories()) {
				SmMachineCategory macCat = (SmMachineCategory) EntityHelper
						.getEntityBySinglePropertyFromList(toRemoveMachCat
								.getPmCategoryL3().getCategoryL3Id(),
								macCategoryList, "pmCategoryL3");
				if (macCat == null) {
					retVal = MessageUtils
							.getMessage(MessageConst.ERROR_722631,
									new String[] {
											toRemoveMachCat.getPmCategoryL3()
													.getFullCatcode(),
											categoryTypeDesc },
									MessageUtils.BUNDLE_COMMON_MESSAGES,
									new FacesScopes().getUserLocale());
				}
			}
		}

		return retVal;
	}

	/**
	 * Validate branch GST info is required if HQ had GST info
	 * 
	 * @param SmSupplierBranch
	 *            br
	 * @param SmGstSupplier
	 *            brGst
	 * @param SmGstSupplier
	 *            hqGst
	 */
	public static void validateBranchGstInformation(SmSupplierBranch br,
			SmGstSupplier brGst, SmGstSupplier hqGst) {
		if (hqGst != null && !StringUtils.isBlank(hqGst.getGstRegNo())) {
			if (br.getRecordStatusFlg() == DbValueConst.RECORD_STATUS_ACTIVE) {
				if (brGst == null || StringUtils.isBlank(brGst.getGstRegNo())) {
					throw new EpException(SmCommonUtil.getMessage(
							"lbl.sm.gst.branch.req.msg", null));
				} else if (!StringUtils.isBlank(brGst.getGstRegNo())) {

					if (hqGst.getSmSupplier() != null
							&& !StringUtils.isBlank(hqGst.getSmSupplier()
									.getRegNo())) {

						// Validate GST reg no. uniqueness
						SmCommonUtil.validateGstRegNoUniqueness(hqGst
								.getSmSupplier().getRegNo(), brGst
								.getGstRegNo());

						if (!SmCommonUtil.validateGstRegNo(brGst.getGstRegNo())) {
							throw new EpException(MessageConst.ERROR_721027);
						}
					}
				}
			}
		}
	}

	// harris
	public static void validateBranchSstInformation(SmSupplierBranch br,
			SmSstSupplier brSst, SmSstSupplier hqSst) {
		if (hqSst != null && !StringUtils.isBlank(hqSst.getSalestaxregno())) {
			if (br.getRecordStatusFlg() == DbValueConst.RECORD_STATUS_ACTIVE) {
				if (brSst == null
						|| StringUtils.isBlank(brSst.getSalestaxregno())) {
					throw new EpException(SmCommonUtil.getMessage(
							"lbl.sm.gst.branch.req.msg", null));
				} else if (!StringUtils.isBlank(brSst.getSalestaxregno())) {

					if (hqSst.getSmSupplier() != null
							&& !StringUtils.isBlank(hqSst.getSmSupplier()
									.getRegNo())) {

						// Validate GST reg no. uniqueness
						SmCommonUtil.validateSstRegNoUniqueness(hqSst
								.getSmSupplier().getRegNo(), brSst
								.getSalestaxregno());
						SmCommonUtil.validateSstServRegNoUniqueness(hqSst
								.getSmSupplier().getRegNo(), brSst
								.getServtaxregno());

						if (!SmCommonUtil.validateSstRegNo(brSst
								.getSalestaxregno())) {
							throw new EpException(MessageConst.ERROR_721027);
						}
						if (!SmCommonUtil.validateSstServRegNo(brSst
								.getServtaxregno())) {
							throw new EpException(MessageConst.ERROR_721027);
						}
					}
				}
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
			SmMachineDtl machine, Long[] selectedMachineCategoryIds) {
		List<SelectItem> machineItems = new ArrayList<SelectItem>();
		if (machine != null) {
			if (machine.getCategoryType() != null
					&& machine.getCategoryType().getParameterId() != null
					&& !machine.getCategoryType().getParameterCode()
							.equals(ParameterConst.CATEGORY_TYPE_PRINTING)) {
				machineItems = SmCommonUtil.getInstance()
						.getMachineItemsByCatTypeCode(
								machine.getCategoryType().getParameterCode());
			} else {
				if (selectedMachineCategoryIds != null) {
					CategoryCodeReqDTO reqDTO = new CategoryCodeReqDTO();
					PmCategoryL3 l3 = null;
					for (Long id : Arrays.asList(selectedMachineCategoryIds)) {
						reqDTO.setCategoryL3Id(id);
						l3 = SmServiceLookup.getCategoryCodeSO()
								.retrieveCategoryL3ById(reqDTO).getCategoryL3();
						machineItems.addAll(SmCommonUtil
								.getPrintingMachineItemsByCategoryCode(l3));
					}
				}
			}
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

	/**
	 * Generate suppoting document check list to PDF
	 * 
	 * @param SmApplicationManagedBean
	 *            bean
	 * @param String
	 *            responseType
	 */
	public static void generateOfflineSuppDocChecklistPdf(
			List<SmApplSupportDoc> docList, SmAppl smAppl, String responseType) {
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
			context.put("supportDocs", docList);
			context.put("docDesc", SmCommonUtil.getMessage(
					"lbl.sm.suppDoc.document.description", null));
			context.put("poRemarks",
					SmCommonUtil.getMessage("lbl.sm.sectionPoRmrk", null));
			context.put("responseType", responseType);
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
			//FacesScopes fs = new FacesScopes();
			// context.put("hostUrl", fs.getRequest().getServerName() + ":"
			// + fs.getRequest().getServerPort());
			context.put("hostUrl", SmCommonUtil.getBasePortalUrl());
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
				context.put("signatories",
						supplierBank.getSmChequeSignatories());
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
	 * Assign machine category default value during edit Equipment
	 * 
	 * @param SmApplicationManagedBean
	 *            bean
	 * @param SmMachineDtl
	 *            mach
	 */
	public static void assignMachineCategoryIds(SmApplicationManagedBean bean,
			SmMachineDtl mach) {
		bean.setSelectedMachineCategoryIds(null);
		// assign category to facility
		Long[] machCatIds = null;
		if (EntityHelper.isCollectionEmpty(mach.getSmMachineCategories())) {
			machCatIds = new Long[0];
		} else {
			machCatIds = new Long[mach.getSmMachineCategories().size()];
			for (int i = 0; i < machCatIds.length; i++) {
				machCatIds[i] = mach.getSmMachineCategories().get(i)
						.getPmCategoryL3().getCategoryL3Id();

			}
		}
		bean.setSelectedMachineCategoryIds(machCatIds);
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

	/**
	 * Add or update machine category during add new equipment or update
	 * existing equipment
	 * 
	 * @param SmApplicationManagedBean
	 *            bean
	 * @param SmA
	 *            smFac
	 */
	public static void addOrUpdateMachineDtlCategory(
			SmApplicationManagedBean bean, SmMachineDtl smMach) {
		if (bean.getSelectedMachineCategoryIds() != null) {
			SmMachineCategory machCat = null;
			if (EntityHelper.isCollectionEmpty(smMach.getSmMachineCategories())) {
				smMach.setSmMachineCategories(new ArrayList<SmMachineCategory>());
			}
			for (Long catId : bean.getSelectedMachineCategoryIds()) {
				for (SmSupplierCategory cat : bean.getSmAppl()
						.getSmSupplierCategories()) {

					// insert new selected category
					if (cat.getPmCategoryL3().getCategoryL3Id().equals(catId)) {
						machCat = (SmMachineCategory) EntityHelper
								.getEntityBySinglePropertyFromList(catId,
										smMach.getSmMachineCategories(),
										"pmCategoryL3");
						if (machCat == null) {
							machCat = new SmMachineCategory();
							machCat.setSmMachineDtl(smMach);
							machCat.setPmCategoryL3(cat.getPmCategoryL3());
							smMach.getSmMachineCategories().add(machCat);
						}
					}
				}
			}

			// remove not selected category
			if (!EntityHelper
					.isCollectionEmpty(smMach.getSmMachineCategories())) {
				Iterator<SmMachineCategory> removeCatFacIt = smMach
						.getSmMachineCategories().iterator();
				while (removeCatFacIt.hasNext()) {
					SmMachineCategory removeCatFac = removeCatFacIt.next();

					if (!EntityHelper.isIdExistInIds(removeCatFac
							.getPmCategoryL3().getCategoryL3Id(), bean
							.getSelectedMachineCategoryIds())) {
						removeCatFacIt.remove();
					}
				}
			}

		} else {
			// none selected so remove all
			smMach.setSmMachineCategories(null);
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

	private static VelocityContext setSignatories(VelocityContext context, List<SmChequeSignatory> smChequeSignatories) {
		for (int i = 0; i < smChequeSignatories.size(); i++) {
			SmChequeSignatory smChequeSignatory = smChequeSignatories.get(i);
			context.put("csNo" + i, i + 1);
			context.put("csName" + i, smChequeSignatory.getSmPersonnel().getName());
			context.put("csIc" + i, smChequeSignatory.getSmPersonnel().getIdentificationNo());
			//System.out.println("Signatory " + i + ": "+ smChequeSignatory.getSmPersonnel().getName() + " : " + smChequeSignatory.getSmPersonnel().getIdentificationNo());
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
		streetAddress = checkAddressSuffix(address.getAddress1()) + " "
				+ checkAddressSuffix(address.getAddress2()) + " "
				+ checkAddressSuffix(address.getAddress3());

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
	 * @return PmStatusDesc
	 */
	public static PmStatusDesc getCurrRegStatusDesc(SmAppl appl) {
		PmStatusDesc retVal = null;
		String language = new FacesScopes().getUserLocale().getLanguage();

		try {
			if (appl != null
					&& appl.getSmSupplier() != null
					&& appl.getSmSupplier().getSmAppl().getPmRegStatus() != null
					&& appl.getSmSupplier().getSmAppl().getPmRegStatus()
							.getPmStatus() != null
					&& appl.getSmSupplier().getSmAppl().getPmRegStatus()
							.getPmStatus().getStatusId() != null) {
				StatusReqDTO reqDTO = new StatusReqDTO();
				reqDTO.setStatusId(appl.getSmSupplier().getSmAppl()
						.getPmRegStatus().getPmStatus().getStatusId());
				reqDTO.setLanguageCode(language);
				retVal = SmServiceLookup.getStatusSO()
						.retrieveStatusDescById(reqDTO).getPmStatusDesc();
			}
		} catch (Exception ex) {

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
			List<SmApplSupportDoc> docs, String suppDocMode, boolean approver) {
		boolean retVal = true;
		if (!EntityHelper.isCollectionEmpty(docs)) {
			for (SmApplSupportDoc doc : docs) {
				if ((approver && (doc.getApproverVerifiedFlg() == null || doc
						.getApproverVerifiedFlg() == 0))
						|| (!approver && (doc.getVerifiedFlg() == null || doc
								.getVerifiedFlg() == 0))) {
					if (suppDocMode != null
							&& suppDocMode
									.equals(SupplierManagementConst.SUPPORTING_DOC_ONLINE)) {
						if (!EntityHelper.isCollectionEmpty(doc
								.getSmApplDocFiles())
								&& !StringUtils.isBlank(doc.getSmApplDocFiles()
										.get(0).getFileName())) {
							retVal = false;
							break;
						}
					} else {
						if (doc.getPmDocument().isMandatory()) {
							retVal = false;
							break;
						}
					}

				} else if ((approver && doc.getApproverVerifiedFlg() == 0)
						|| (!approver && doc.getVerifiedFlg() == 0)) {
					retVal = false;
					break;
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
									"lbl.sm.staffinfo.semiprof",
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

	/**
	 * Construct and get category and facility list to display in facility
	 * section
	 * 
	 * @param SmAppl
	 *            smAppl
	 * @return List<SmCategoryFacilityVO>
	 */
	public static List<PmCategoryL3> getCategoryFacilityVO(SmAppl smAppl,
			String facType) {
		List<PmCategoryL3> categoryFacilityVOList = new ArrayList<PmCategoryL3>();
		if (smAppl != null
				&& !EntityHelper.isCollectionEmpty(smAppl
						.getSmSupplierCategories())) {
			for (SmSupplierCategory cat : smAppl.getSmSupplierCategories()) {
				//#32008 - after approval bug, #28775
				if (isValidCategory(cat, smAppl)
						&& (cat.getPmCategoryL3() != null
								&& !StringUtils.isBlank(cat.getPmCategoryL3()
										.getFacilityType()) && cat
								.getPmCategoryL3().getFacilityType()
								.equalsIgnoreCase(facType))
						//#30766 & #34209 - START
						&& ( ((cat.getApprvrDecFlg()!=null && cat.getApprvrDecFlg() == 1) || (cat.getPoDecFlg()!=null && cat.getPoDecFlg()==1)) ||  (cat.getPoDecFlg()==null && cat.getApprvrDecFlg()==null))		
						) {
						//#30766 & #34209 - END
					cat.getPmCategoryL3().setFacilityList(
							new ArrayList<SmFacility>());
					cat.getPmCategoryL3().setSelectedFacilityList(
							new ArrayList<SmFacility>());

					for (SmFacility fac : smAppl.getSmFacilities()) {
						if (!EntityHelper.isCollectionEmpty(fac
								.getSmSupplierCategoryFacilities())) {
							SmSupplierCategoryFacility catTmp = (SmSupplierCategoryFacility) EntityHelper
									.getEntityBySinglePropertyFromList(cat
											.getPmCategoryL3()
											.getCategoryL3Id(), fac
											.getSmSupplierCategoryFacilities(),
											"pmCategoryL3");

							if (catTmp != null) {
								SmFacility facTmp = (SmFacility) EntityHelper
										.getEntityBySinglePropertyFromList(fac
												.getFacilityId(), cat
												.getPmCategoryL3()
												.getFacilityList(),
												"facilityId");
								if (facTmp == null) {
									cat.getPmCategoryL3().getFacilityList()
											.add(fac);
								}
							}
						}
					}

					categoryFacilityVOList.add(cat.getPmCategoryL3());
				}
			}
		}
		return categoryFacilityVOList;
	}

	public static List<SmMachineCategoryVO> getMachineCategoryVO(SmAppl smAppl) {
		List<SmMachineCategoryVO> machineCategoryVOList = new ArrayList<SmMachineCategoryVO>();
		if (smAppl != null
				&& !EntityHelper.isCollectionEmpty(smAppl
						.getSmSupplierCategories())) {
			SmMachineCategoryVO machineCategoryVO = null;
			SmMachineDtl machDtlTmp = null;
			boolean isNew = false;
			for (SmSupplierCategory cat : smAppl.getSmSupplierCategories()) {
				//#32008 - after approval bug
				if ((cat.getRecordStatusFlg().intValue() == SupplierManagementConst.PENDING_ACT_STATUS 
						|| cat.getRecordStatusFlg().intValue() == SupplierManagementConst.PENDING_RE_APPROVAL_STATUS
						|| isActiveCategoryAfterApproval(cat, smAppl.getApplId()))
						&& cat.getPmCategoryL3() != null
						&& cat.getPmCategoryL3().getCategoryTypeCode() != null
						&& Arrays
								.asList(my.ep.constant.sm.SupplierManagementConst.SM_CAT_TYPE_MACHINERY)
								.contains(
										cat.getPmCategoryL3()
												.getCategoryTypeCode()
												.getParameterCode())
						//#30766 & #34209 - START
						&& ( ((cat.getApprvrDecFlg()!=null && cat.getApprvrDecFlg() == 1) || (cat.getPoDecFlg()!=null && cat.getPoDecFlg()==1)) ||  (cat.getPoDecFlg()==null && cat.getApprvrDecFlg()==null))) { 
						//#30766 & #34209 - END
					
					machineCategoryVO = (SmMachineCategoryVO) EntityHelper
							.getEntityBySinglePropertyFromList(cat
									.getPmCategoryL3().getCategoryTypeCode()
									.getParameterId(), machineCategoryVOList,
									"categoryTypeCode");

					if (machineCategoryVO == null) {
						machineCategoryVO = new SmMachineCategoryVO();
						machineCategoryVO.setCategoryTypeCode(cat
								.getPmCategoryL3().getCategoryTypeCode());
						machineCategoryVO
								.setSmMachineDtls(new ArrayList<SmMachineDtl>());
						isNew = true;
					}

					for (SmMachineDtl mach : smAppl.getSmMachineDtls()) {
						//Bug #26616
						if (mach.getCategoryType() != null) {
							if (mach.getCategoryType()
									.getParameterId()
									.compareTo(
											cat.getPmCategoryL3()
													.getCategoryTypeCode()
													.getParameterId()) == 0) {
								machDtlTmp = (SmMachineDtl) EntityHelper
										.getEntityBySinglePropertyFromList(mach
												.getMachineDtlId(),
												machineCategoryVO
														.getSmMachineDtls(),
												"machineDtlId");
								if (mach.getSmMachineCategories() != null
										&& mach.getSmMachineCategories().size() > 0) {
									boolean isCategoryExist = false;
									for (SmMachineCategory machCat : mach
											.getSmMachineCategories()) {
										String macCatCode = machCat
												.getPmCategoryL3()
												.getPmCategoryL2()
												.getPmCategoryL1()
												.getCategoryL1Code()
												.concat(machCat
														.getPmCategoryL3()
														.getPmCategoryL2()
														.getCategoryL2Code())
												.concat(machCat
														.getPmCategoryL3()
														.getCategoryL3Code());
										String suppCatCode = cat
												.getPmCategoryL3()
												.getPmCategoryL2()
												.getPmCategoryL1()
												.getCategoryL1Code()
												.concat(cat.getPmCategoryL3()
														.getPmCategoryL2()
														.getCategoryL2Code())
												.concat(cat.getPmCategoryL3()
														.getCategoryL3Code());
										if (macCatCode
												.equalsIgnoreCase(suppCatCode)) {
											isCategoryExist = true;
											break;
										}
									}
									if (machDtlTmp == null && isCategoryExist) {
										machineCategoryVO.getSmMachineDtls()
												.add(mach);
									}
								}
							}
						}
					}

					if (isNew) {
						machineCategoryVOList.add(machineCategoryVO);
						isNew = false;
					}
				}
			}
		}
		return machineCategoryVOList;
	}

	/**
	 * Construct assign site visit VO to display in Site Visit Assignment Popup
	 * 
	 * @param SmAppl
	 *            smAppl
	 * @param List
	 *            <PmCategoryL3> l3List
	 * @return List<SmSiteVisitFacilityVO>
	 */
	public static List<SmSiteVisitFacilityVO> getSiteVisitFacilityVO(
			SmAppl smAppl, List<PmCategoryL3> l3List) {
		List<SmSiteVisitFacilityVO> retVal = new ArrayList<SmSiteVisitFacilityVO>();
		if (!EntityHelper.isCollectionEmpty(l3List)) {
			SmSiteVisitFacilityVO vo = null;
			for (PmCategoryL3 l3 : l3List) {
				if (l3.getFacilityType()
						.equals(SupplierManagementConst.SM_FACILITY_TYPE_BUSSINESSOFFICE)) {

					vo = new SmSiteVisitFacilityVO();
					vo.setL3(l3);

					// add HQ
					vo.setHeadQuarters(new ArrayList<SmCompanyAddress>());
					vo.getHeadQuarters().add(
							(SmCompanyAddress) SerializationUtils.clone(smAppl
									.getSmCompanyBasic()
									.getSmCompanyAddresses().get(0)));
					retVal.add(vo);
					vo = cloneFacilities(l3, vo);		//#28775
				} else {
					if (!EntityHelper.isCollectionEmpty(l3.getFacilityList())) {
						vo = new SmSiteVisitFacilityVO();
						vo.setL3(l3);

						// add Faciliies
						vo = cloneFacilities(l3, vo);		//#28775

						// add HQ
						vo.setHeadQuarters(new ArrayList<SmCompanyAddress>());
						vo.getHeadQuarters()
								.add((SmCompanyAddress) SerializationUtils
										.clone(smAppl.getSmCompanyBasic()
												.getSmCompanyAddresses().get(0)));

						// add associate company
						vo.setAssocCompanies(new ArrayList<SmAssociateCompany>());
						if (!EntityHelper.isCollectionEmpty(smAppl
								.getSmAssociateCompanies())) {
							for (SmAssociateCompany assoc : smAppl
									.getSmAssociateCompanies()) {
								vo.getAssocCompanies().add(
										(SmAssociateCompany) SerializationUtils
												.clone(assoc));
							}
						}

						// add branch
						vo.setSuppBranches(new ArrayList<SmSupplierBranch>());
						if (!EntityHelper.isCollectionEmpty(smAppl
								.getSmSupplierBranches())) {
							for (SmSupplierBranch br : smAppl
									.getSmSupplierBranches()) {
								vo.getSuppBranches().add(
										(SmSupplierBranch) SerializationUtils
												.clone(br));
							}
						}
						retVal.add(vo);
					}
				}
			}
		}
		return retVal;
	}

	//#28775
	private static SmSiteVisitFacilityVO cloneFacilities(PmCategoryL3 l3, SmSiteVisitFacilityVO vo){
		if (l3 == null || EntityHelper.isCollectionEmpty(l3.getFacilityList())){
			return vo;
		}
		vo.setFacilities(new ArrayList<SmFacility>());
		for (SmFacility fac : l3.getFacilityList()) {

			// check whether category share address with other categories
			if (!EntityHelper.isCollectionEmpty(fac.getSmSupplierCategoryFacilities())
					&& fac.getSmSupplierCategoryFacilities().size() > 1) {
				vo.setShareAddress(true);
			}
			vo.getFacilities().add((SmFacility) SerializationUtils.clone(fac));
		}
		return vo;
	}

	/**
	 * Initiate MOF application new BPM Task
	 * 
	 * @param SmAppl
	 *            smAppl
	 * @return
	 */
	public static SmAppl initiateMofApplNewBpmTask(SmAppl smAppl) {
		FacesScopes fs = new FacesScopes();
		List<String> userList = new ArrayList<String>();
		PmUser loginUser = fs.getUserSessionBean();
		userList.add(loginUser.getLoginId());
		MofRegistrationReqDTO reqDTO = new MofRegistrationReqDTO();
		reqDTO.setSmAppl(smAppl);
		reqDTO.setUserList(userList);
		reqDTO.setToken(fs.getBPMAuthenticationCtxToken());
		return SmServiceLookup.getMofRegistrationSO()
				.initiateMofApplNewBpmTask(reqDTO).getSmAppl();
	}

	/**
	 * Get all MOF application type save as draft application URL
	 * 
	 * @param SmAppl
	 *            smAppl
	 * @return String
	 */
	public static String getMofApplicationSaveDraftUrl(SmAppl smAppl) {
		String parameter = StringUtils.EMPTY;
		String urlParam = SmCommonUtil.getBasePortalAppUrlFromProperties()
				+ "task-list";

		try {
			WorklistItemVO item = SmCommonUtil.getBpmAssignedTaskByDocNo(smAppl
					.getApplNo());
			if (item != null && !StringUtils.isBlank(item.getTaskId())) {
				parameter = "taskId=" + item.getTaskId() + "&claim=false";
				urlParam = SmCommonUtil.getBasePortalAppUrlFromProperties()
						+ "mof-application?" + parameter;
			} else {
				SmPersonnel adminPersonnel = SmApplicationUtil
						.getAdminPersonnel(smAppl.getSmPersonnels());
				if (adminPersonnel != null
						&& adminPersonnel.getPmUser() != null
						&& !StringUtils.isBlank(adminPersonnel.getPmUser()
								.getLoginId())) {
					List<WorklistItemVO> workList = SmCommonUtil
							.getBpmTaskListByLoginId(adminPersonnel.getPmUser()
									.getLoginId());
					if (!EntityHelper.isCollectionEmpty(workList)) {
						for (WorklistItemVO tmp : workList) {
							if (tmp.getDocNumber().equals(smAppl.getApplNo())) {
								item = tmp;
								break;
							}
						}
						if (item != null) {
							parameter = "taskId=" + item.getTaskId()
									+ "&claim=false";
							urlParam = SmCommonUtil
									.getBasePortalAppUrlFromProperties()
									+ "mof-application?" + parameter;
						}
					}
				}
			}
		} catch (Exception ex) {
			log.debug(ex.getMessage());
		}

		return urlParam;
	}

	/**
	 * Update all application supplier branch code if empty
	 * 
	 * @param SmAppl
	 *            smAppl
	 */
	public static void updateApplBranchCodeIfEmpty(SmAppl smAppl) {
		if (!EntityHelper.isCollectionEmpty(smAppl.getSmSupplierBranches())) {
			for (SmSupplierBranch br : smAppl.getSmSupplierBranches()) {
				if (br.getBranchCode() == null) {
					br.setBranchCode(br.getSupplierBranchId());
				}
			}
		}
	}

	public static void approveApplCategories(boolean isApprover, boolean isPo,
			SmAppl smAppl) {
		for (SmSupplierCategory cat : smAppl.getSmSupplierCategories()) {
			if (cat.getRecordStatusFlg().compareTo(
					SupplierManagementConst.PENDING_ACT_STATUS) == 0
					|| cat.getRecordStatusFlg().compareTo(
							SupplierManagementConst.PENDING_RE_APPROVAL_STATUS) == 0) {
				if ((isPo && cat.getPoDecFlg() != null && cat.getPoDecFlg()
						.compareTo(new Integer(1)) == 0)
						|| (isApprover && cat.getApprvrDecFlg() != null && cat
								.getApprvrDecFlg().compareTo(new Integer(1)) == 0)) {
					cat.setApprovedDate(new Date());
					cat.setRecordStatusFlg(SupplierManagementConst.APPROVED_RECORD_STATUS);
					setSupplierCategoryEffectiveDate(cat, smAppl.getApplType());
				} else {
					cat.setRecordStatusFlg(DbValueConst.RECORD_STATUS_SUSPENDED);
				}

				// if PO direct approved, delete Approver Flag
				if (isPo) {
					cat.setApprvrDecFlg(null);
					cat.setApproverRemarks(null);
				}
				setCategoryNameForApprovedCategory(cat);
			}
		}
	}

	public static void initNewSmPersonnel(SmApplicationManagedBean bean,
			int currTab) {
		SmPersonnel shr = new SmPersonnel();
		shr.setEpRole("");
		shr.setShareAmt(new BigDecimal(0));
		shr.setSharePercentage(new BigDecimal(0));
		setDefaultPersonnelValues(shr, bean.getSmAppl());

		if (bean.getCurrentPageType().equalsIgnoreCase(
				SupplierManagementConst.SM_APPL_PAGE_TYPE_EQUITY)) {
			validatePaidCaptialAmt(false, bean, currTab);
			bean.setValidEquityLimit(SmApplicationUtil.manageEquityLimit(shr,
					bean.getBusinessTypeVal(), bean.getSmapplShareHolders(),bean.getSmSupplier()));//change by fahmi, log : 23858 13/03/2019 : add smSupplier
			if (!bean.isValidEquityLimit()) {
				if (bean.getBusinessTypeVal()
						.equalsIgnoreCase(
								SupplierManagementConst.SM_SUPPLIER_BUSINESS_TYPE_ROB_INDIVIDUAL_DEFAULT) && (bean.getSmSupplier() != null && !SupplierManagementConst.SM_SUPPLIER_BUSINESS_TYPE_PROFESSIONAL_BODY.equalsIgnoreCase(bean.getSmSupplier().getBusinessType()))) {
					throw new EpException(
							MessageConst.ERROR_722547,
							new String[] { String.valueOf(SmRuleHelper
									.getInstance()
									.getMaxEquityOwnerCountForROBIndividual()) });
				} else {
					throw new EpException(
							MessageConst.ERROR_722547,
							new String[] { String
									.valueOf(SmRuleHelper
											.getInstance()
											.getMaxEquityOwnerCountForOtherThanROBIndividaul()) });
				}//change by fahmi, log : 23858 13/03/2019

			}
			shr.setEquityOwner(true);
			bean.setPersonnelInfoHeader(SmCommonUtil.getMessage(
					"lbl.sm.equity.owner.information", null));
		} else if (bean.getCurrentPageType().equalsIgnoreCase(
				SupplierManagementConst.SM_APPL_PAGE_TYPE_PERSONNEL)) {
			bean.setValidEquityLimit(SmApplicationUtil.manageEquityLimit(shr,
					bean.getBusinessTypeVal(), bean.getSmapplShareHolders(),bean.getSmSupplier()));//change by fahmi, log : 23858 13/03/2019 : add smsupplier
			bean.setPersonnelInfoHeader(SmCommonUtil.getMessage(
					"lbl.sm.person.panHdr", null));
		}
		assignSelShareHolderAddress(shr, bean);

		bean.setSelectedShareHolder(shr);
		if (bean.isValidSmUserLimit()) {
			bean.setUserWithRole(false);
		} else {
			bean.setUserWithRole(true);
		}

		// if already reach authorized limit, default authorized flag to 0
		if (bean.isValidSmUserLimit()) {
			bean.getSelectedShareHolder().setAuthorizedFlg(0);
		}
		bean.setePRoleList(getPersonnelRoleItems(bean.getSelectedShareHolder(),
				bean.getSmAppl(), bean.getAuthVO()));

		bean.setRedirectToPersonPage(true);

		// set default nationality to MALAYSIA
		bean.getSelectedShareHolder()
				.setPmCountry(
						SmCommonUtil
								.retrieveCountryByCountryCode(SupplierManagementConst.COUNTRY_MALAYSIA_CODE));
		SmCommonUtil.setDefaultPersonnelNationality(bean
				.getSelectedShareHolder());
		bean.setDisableIcEmail(false);

		// reset personnel view only to false for add new
		bean.setShowPersonnelViewOnly(false);

		// reset personnel name and nationality to false for add new
		bean.setDisabledShareholderPersonnelNameAndNationality(false);

		// reset previous approved personnel because not exist for new personnel
		bean.setLatestPersonnel(null);

		bean.setDisableRoleSelection(disableEpRoleDropDown(
				bean.getSelectedShareHolder(), bean.getPrevApprovedPersonnels()));
		bean.setDisableAuthorizedRadio(isDisableAuthorizedRadio(
				bean.getSelectedShareHolder(), bean.isValidSmUserLimit()));
	}

	/**
	 * Builds the role list.
	 */
	public static List<SelectItem> getPersonnelRoleItems(SmPersonnel personnel,
			SmAppl smAppl, SmAuthorizedPersonnelCountVO authVO) {
		boolean inDbAlreadyAuth = false;
		MofRegistrationReqDTO reqDTO = new MofRegistrationReqDTO();
		reqDTO.setApplId(smAppl.getApplId());
		reqDTO.setPersonnel(personnel);
		inDbAlreadyAuth = SmServiceLookup.getMofRegistrationSO()
				.isPersonnelAuthorizedByIdNoApplId(reqDTO).isValid();
		List<SelectItem> roles = new ArrayList<SelectItem>();

		// If authorized and current role is FL_USER, set to
		// MOF_SUPPLIER_USER
		if (personnel.getAuthorizedFlg() == DbValueConst.RECORD_STATUS_ACTIVE
				&& StringUtils.isNotBlank(personnel.getEpRole())
				&& personnel.getEpRole().equals(RoleCode.FL_USER.value())) {
			personnel.setEpRole(RoleCode.MOF_SUPPLIER_USER.value());
		}
		// if not authorized and current role is MOF_SUPPLIER_USER, set to null
		else if (personnel.getAuthorizedFlg() != DbValueConst.RECORD_STATUS_ACTIVE
				&& StringUtils.isNotBlank(personnel.getEpRole())
				&& personnel.getEpRole().equals(
						RoleCode.MOF_SUPPLIER_USER.value())) {
			personnel.setEpRole(StringUtils.EMPTY);
		}

		// add MOF_SUPPLIER_USER role
		if (!StringUtils.isBlank(personnel.getEpRole())
				&& personnel.getEpRole().equals(
						RoleCode.MOF_SUPPLIER_USER.value())) {
			addRoles(roles, RoleCode.MOF_SUPPLIER_USER.value(), false);
		} else {
			// only add supplier user if not reach limit and authorized selected
			// or the personnel is already authorized in db
			if ((!authVO.isReachAuthorizedPersonnelLimit() && personnel.getAuthorizedFlg() == DbValueConst.RECORD_STATUS_ACTIVE)
					|| inDbAlreadyAuth
					|| (authVO.isReachAuthorizedPersonnelLimit()
							&& smAppl.getSupplierType().equals(SupplierManagementConst.STYPE_CONSULTANT) && personnel
							.getAuthorizedFlg() == 1)) { // #30053 - if reach authorize personnel limit and supplier type consultant
				addRoles(roles, RoleCode.MOF_SUPPLIER_USER.value(), false);
			}
		}

		// add FL_USER role
		if (!smAppl.getSupplierType().equals(SupplierManagementConst.STYPE_CONSULTANT))// #30053
		{	if (!StringUtils.isBlank(personnel.getEpRole()) && personnel.getEpRole().equals(RoleCode.FL_USER.value())) {
				addRoles(roles, RoleCode.FL_USER.value(), false);
			} else {
				if (authVO.isReachAuthorizedPersonnelLimit()
						&& personnel.getAuthorizedFlg() == DbValueConst.RECORD_STATUS_SUSPENDED) {
					// add if already reach limit and personnel role is empty
					if (StringUtils.isBlank(personnel.getEpRole())) {
						addRoles(roles, RoleCode.FL_USER.value(), false);
					}
					// add if already reach limit and personnel role is not
					// already
					// activated as MOF_SUPPLIER_USER
					else if (!StringUtils.isBlank(personnel.getEpRole())
							&& personnel.getEpRole().equals(RoleCode.MOF_SUPPLIER_USER.value())
							&& personnel.getRecordStatusFlg() != DbValueConst.RECORD_STATUS_ACTIVE) {
						addRoles(roles, RoleCode.FL_USER.value(), false);
					}

				}
			}

		}

		// only add CM_USER if authorized and for contractor that transact with
		// federal govt
		if (personnel.getAuthorizedFlg() == DbValueConst.RECORD_STATUS_ACTIVE
				&& smAppl.getSupplierType().equalsIgnoreCase(
						SupplierManagementConst.STYPE_CONTRACTOR)
				&& smAppl.getSmCompanyBasic() != null
				&& smAppl.getSmCompanyBasic().isWithFederal()) {
			addRoles(roles, RoleCode.CM_USER.value(), false);
		}

		// add ADMIN role
		if (personnel.getEpRole() != null
				&& (personnel.getEpRole().equalsIgnoreCase(
						RoleCode.MOF_SUPPLIER_ADMIN.value()) || personnel
						.getEpRole().equalsIgnoreCase(
								RoleCode.SUPPLIER_TEMP.value()))) {
			addRoles(roles, personnel.getEpRole(), true);
		}
		return roles;
	}

	public static boolean isDisableAuthorizedRadio(SmPersonnel personnel,
			boolean isValidSmUserLimit) {
		boolean retVal = false;
		if (!StringUtils.isBlank(personnel.getEpRole())) {
			// Supplier Admin not allowed to change authorized flag
			if (personnel.getEpRole().equals(RoleCode.SUPPLIER_TEMP.value())
					|| personnel.getEpRole().equals(
							RoleCode.MOF_SUPPLIER_ADMIN.value())) {
				retVal = true;
			}
			// disabled authorized flag if soft cert is still processing
			else if (personnel.getSoftcertFlg() == SupplierManagementConst.SOFT_CERT_PROCESSING) {
				retVal = true;
			} else if (isValidSmUserLimit) {
				// new personnel not allowed to change authorized flag if
				// already reach limit
				if (personnel.getPersonnelId() == null) {
					retVal = true;
				}
				// already activated personnel not allowed to change authorized
				// flag if already reach limit
				else if (!StringUtils.isBlank(personnel.getEpRole())
						&& personnel.getRecordStatusFlg() == DbValueConst.RECORD_STATUS_ACTIVE) {
					retVal = true;
				}
				// pending activation FL_USER not allowed to change authorized
				// flag if already reach limit
				else if (personnel.getRecordStatusFlg() != DbValueConst.RECORD_STATUS_ACTIVE
						&& !StringUtils.isBlank(personnel.getEpRole())
						&& personnel.getEpRole().equals(
								RoleCode.FL_USER.value())) {
					retVal = true;
				}
			}
		} else {
			// if no epRole and already reach limit
			if (isValidSmUserLimit
					&& !personnel.getSmAppl().getSupplierType().equals(SupplierManagementConst.STYPE_CONSULTANT)) { //#30053
				retVal = true;
			}
		}
		return retVal;
	}

	/**
	 * Assign selected personnel object to managed bean for editing the
	 * information.
	 * 
	 * @param shareHolderBean
	 *            - SmPersonnel object
	 */
	public static void assignSelShareHolderAddress(SmPersonnel shareHolderBean,
			SmApplicationManagedBean bean) {
		//#13320 - view only && #29086
		if (isPersonnelViewOnly(shareHolderBean) || bean.isDisAllowOwnerEdit()
				&& shareHolderBean.getPersonnelId() != null) {
			if (shareHolderBean.getSmPersonnelAddresses() != null
					&& shareHolderBean.getSmPersonnelAddresses().size() > 0) {
				for (SmPersonnelAddress personnelAddress : shareHolderBean
						.getSmPersonnelAddresses()) {
					if (personnelAddress
							.getAddressType()
							.equalsIgnoreCase(
									SupplierManagementConst.SM_PERSONNEL_ADDR_TYPE_PERM)) {
						if (personnelAddress.getSmAddress() != null
								&& personnelAddress.getSmAddress()
										.getAddressId() != null) {
							bean.setShareHolderPermAddr(personnelAddress);
						}
					} else if (personnelAddress
							.getAddressType()
							.equalsIgnoreCase(
									SupplierManagementConst.SM_PERSONNEL_ADDR_TYPE_MAIL)) {
						if (personnelAddress.getSmAddress() != null
								&& personnelAddress.getSmAddress()
										.getAddressId() != null) {
							bean.setShareHolderMailAddr(personnelAddress);
						}
					} else if (personnelAddress
							.getAddressType()
							.equalsIgnoreCase(
									SupplierManagementConst.SM_PERSONNEL_ADDR_TYPE_RES)) {
						if (personnelAddress.getSmAddress() != null
								&& personnelAddress.getSmAddress()
										.getAddressId() != null) {
							bean.setShareHolderResdAddr(personnelAddress);
						}
					} else if (personnelAddress
							.getAddressType()
							.equalsIgnoreCase(
									SupplierManagementConst.SM_PERSONNEL_ADDR_TYPE_OFFICE)) {
						if (personnelAddress.getSmAddress() != null
								&& personnelAddress.getSmAddress()
										.getAddressId() != null) {
							bean.setShareHolderOffAddr(personnelAddress);
						}
					}
				}
			}
		} else {
			SmPersonnelAddress perm = new SmPersonnelAddress();
			perm.setAddressType(SupplierManagementConst.SM_PERSONNEL_ADDR_TYPE_PERM);
			perm.setSmAddress(SmApplicationUtil.setDefaultAddr(null));
			perm.setSmPersonnel(shareHolderBean);
			perm.setSameAsPerm(false);

			SmPersonnelAddress mail = new SmPersonnelAddress();
			mail.setAddressType(SupplierManagementConst.SM_PERSONNEL_ADDR_TYPE_MAIL);
			mail.setSmAddress(SmApplicationUtil.setDefaultAddr(null));
			mail.setSmPersonnel(shareHolderBean);
			mail.setSameAsPerm(false);

			SmPersonnelAddress off = new SmPersonnelAddress();
			off.setAddressType(SupplierManagementConst.SM_PERSONNEL_ADDR_TYPE_OFFICE);
			off.setSmAddress(SmApplicationUtil.setDefaultAddr(null));
			off.setSmPersonnel(shareHolderBean);
			off.setSameAsPerm(false);

			SmPersonnelAddress res = new SmPersonnelAddress();
			res.setAddressType(SupplierManagementConst.SM_PERSONNEL_ADDR_TYPE_RES);
			res.setSmAddress(SmApplicationUtil.setDefaultAddr(null));
			res.setSmPersonnel(shareHolderBean);
			res.setSameAsPerm(false);

			bean.setShareHolderPermAddr(perm);
			bean.setShareHolderMailAddr(mail);
			bean.setShareHolderOffAddr(off);
			bean.setShareHolderResdAddr(res);
			List<SelectItem> defStateItems = SmCommonUtil.getStateList(perm
					.getSmAddress().getPmCountry().getCountryId());
			if (shareHolderBean.getSmPersonnelAddresses().isEmpty()) {

				// reset to default values for a new record
				bean.setShrHldrPermStates(defStateItems);
				bean.setShrHldrPermDistricts(new ArrayList<SelectItem>());
				bean.setShrHldrPermCities(new ArrayList<SelectItem>());
				bean.setShrHldrMailStates(defStateItems);
				bean.setShrHldrMailDistricts(new ArrayList<SelectItem>());
				bean.setShrHldrMailCities(new ArrayList<SelectItem>());
				bean.setShrHldrResdStates(defStateItems);
				bean.setShrHldrResdDistricts(new ArrayList<SelectItem>());
				bean.setShrHldrResdCities(new ArrayList<SelectItem>());
				bean.setShrHldrOffStates(defStateItems);
				bean.setShrHldrOffDistricts(new ArrayList<SelectItem>());
				bean.setShrHldrOffCities(new ArrayList<SelectItem>());

			} else {
				for (SmPersonnelAddress addr : shareHolderBean
						.getSmPersonnelAddresses()) {
					SmAddress smAddress = new SmAddress();
					smAddress.setAddress1(addr.getSmAddress().getAddress1());
					smAddress.setAddress2(addr.getSmAddress().getAddress2());
					smAddress.setAddress3(addr.getSmAddress().getAddress3());
					//START SSM INTEGRATION
					smAddress.setSsmData(addr.getSmAddress().isSsmData());
					smAddress.setAddressSsmNull(addr.getSmAddress().isAddressSsmNull());
					smAddress.setPostcodeSsmNull(addr.getSmAddress().isPostcodeSsmNull());
					smAddress.setStateSsmNull(addr.getSmAddress().isStateSsmNull());
					smAddress.setCountrySsmNull(addr.getSmAddress().isCountrySsmNull());
					
					if(addr.getSmAddress().getPmCountry() != null){
						PmCountry country = new PmCountry();
						country.setCountryId(addr.getSmAddress().getPmCountry()
								.getCountryId());
						smAddress.setPmCountry(country);
						smAddress.setCountryId(country.getCountryId());
					}else{
						if(addr.getSmAddress().getPmState() != null){
							PmCountry country = SmCommonUtil.retrieveCountryByCountryCode(SupplierManagementConst.COUNTRY_MALAYSIA_CODE);
							country.setCountryId(country
									.getCountryId());
							smAddress.setPmCountry(country);
							smAddress.setCountryId(country.getCountryId());
						}
					}
					//END SSM INTEGRATION
					PmState state = new PmState();
					state.setStateId(addr.getSmAddress().getPmState()
							.getStateId());
					smAddress.setPmState(state);
					smAddress.setStateId(state.getStateId());
					//START 27879
					//#13320 - added division - start
					if(addr.getSmAddress().getPmDivision() != null){
						PmDivision div = new PmDivision();
						div.setDivisionId(addr.getSmAddress().getPmDivision()
								.getDivisionId());
						smAddress.setPmDivision(div);
						smAddress.setDivisionId(div.getDivisionId());
					}
					//#13320 - added division - end
					if(addr.getSmAddress().getPmDistrict() != null){
						PmDistrict district = new PmDistrict();

						district.setDistrictId(addr.getSmAddress().getPmDistrict()
								.getDistrictId());
						smAddress.setPmDistrict(district);
						smAddress.setDistrictId(district.getDistrictId());
					}
					
					if(addr.getSmAddress().getPmCity() != null){
						PmCity city = new PmCity();
						city.setCityId(addr.getSmAddress().getPmCity().getCityId());
						smAddress.setPmCity(city);
						smAddress.setCityId(city.getCityId());
					}
					//END 27879
					smAddress.setPostcode(addr.getSmAddress().getPostcode());
					addr.setSmAddress(smAddress);

					if (addr.getAddressType()
							.equalsIgnoreCase(
									SupplierManagementConst.SM_PERSONNEL_ADDR_TYPE_PERM)) {
						bean.setShareHolderPermAddr(addr);
						SmApplicationUtil
								.getInstance()
								.assignStateDistAndCityForSelVal(
										bean,
										bean.getShareHolderPermAddr()
												.getSmAddress(),
										SupplierManagementConst.SM_SHRHLDR_PERM_ADDR_PANEL);

					}
					if (addr.getAddressType()
							.equalsIgnoreCase(
									SupplierManagementConst.SM_PERSONNEL_ADDR_TYPE_MAIL)) {
						bean.setShareHolderMailAddr(addr);
						SmApplicationUtil
								.getInstance()
								.assignStateDistAndCityForSelVal(
										bean,
										bean.getShareHolderMailAddr()
												.getSmAddress(),
										SupplierManagementConst.SM_SHRHLDR_MAIL_ADDR_PANEL);

					}
					if (addr.getAddressType().equalsIgnoreCase(
							SupplierManagementConst.SM_PERSONNEL_ADDR_TYPE_RES)) {
						bean.setShareHolderResdAddr(addr);
						SmApplicationUtil
								.getInstance()
								.assignStateDistAndCityForSelVal(
										bean,
										bean.getShareHolderResdAddr()
												.getSmAddress(),
										SupplierManagementConst.SM_SHRHLDR_RESD_ADDR_PANEL);
						//START 27495
						//to check, if district state not equal to pm state set district to null & City
						if(bean.getShareHolderResdAddr().getSmAddress().isSsmData()){
							SmAddress address = bean.getShareHolderResdAddr().getSmAddress();
							if(address != null){
								if(address.getPmState()!= null && address.getPmDistrict() != null){
									if(address.getPmDistrict().getPmState() != null){
										//#34138
										if(!address.getPmDistrict().getPmState().getStateId().equals(address.getPmState().getStateId())){
											address.setPmDistrict(new PmDistrict());
											address.setPmCity(new PmCity());
										}
									}
								}
							}
						}
						//END 27495

					}
					if (addr.getAddressType()
							.equalsIgnoreCase(
									SupplierManagementConst.SM_PERSONNEL_ADDR_TYPE_OFFICE)) {
						bean.setShareHolderOffAddr(addr);
						SmApplicationUtil
								.getInstance()
								.assignStateDistAndCityForSelVal(
										bean,
										bean.getShareHolderOffAddr()
												.getSmAddress(),
										SupplierManagementConst.SM_SHRHLDR_OFF_ADDR_PANEL);
					}
				}

				if ((bean.getShrHldrPermStates() == null)
						|| (bean.getShrHldrPermStates() != null && bean
								.getShrHldrPermStates().isEmpty())) {
					bean.setShrHldrPermStates(defStateItems);
					bean.setShrHldrPermDistricts(new ArrayList<SelectItem>());
					bean.setShrHldrPermCities(new ArrayList<SelectItem>());
				}
				if ((bean.getShrHldrMailStates() == null)
						|| (bean.getShrHldrMailStates() != null && bean
								.getShrHldrMailStates().isEmpty())) {
					bean.setShrHldrMailStates(defStateItems);
					bean.setShrHldrMailDistricts(new ArrayList<SelectItem>());
					bean.setShrHldrMailCities(new ArrayList<SelectItem>());
				}
				if ((bean.getShrHldrResdStates() == null)
						|| (bean.getShrHldrResdStates() != null && bean
								.getShrHldrResdStates().isEmpty())) {
					bean.setShrHldrResdStates(defStateItems);
					bean.setShrHldrResdDistricts(new ArrayList<SelectItem>());
					bean.setShrHldrResdCities(new ArrayList<SelectItem>());
				}
				if ((bean.getShrHldrOffStates() == null)
						|| (bean.getShrHldrOffStates() != null && bean
								.getShrHldrOffStates().isEmpty())) {
					bean.setShrHldrOffStates(defStateItems);
					bean.setShrHldrOffDistricts(new ArrayList<SelectItem>());
					bean.setShrHldrOffCities(new ArrayList<SelectItem>());
				}

			}
		}
		bean.setSelectedShareHolder(shareHolderBean);
	}

	/**
	 * Ajax call to assign selected personnel privilege
	 * 
	 * @param e
	 *            - AjaxBehaviorEvent object
	 */
	public static void assignShareHolderPrivilege(SmApplicationManagedBean bean) {
		if (bean.getSelectedShareHolder().getAuthorizedFlg() == DbValueConst.RECORD_STATUS_SUSPENDED) {
			bean.getSelectedShareHolder().setContractSignerFlg(
					DbValueConst.RECORD_STATUS_SUSPENDED);
			bean.getShareHolderMailAddr().setSameAsPerm(true);
		} else {
			bean.getShareHolderMailAddr().setSameAsPerm(false);
			assignSelShareHolderAddress(bean.getSelectedShareHolder(), bean);
		}
		bean.setePRoleList(SmApplicationUtil.getPersonnelRoleItems(
				bean.getSelectedShareHolder(), bean.getSmAppl(),
				bean.getAuthVO()));
		bean.setDisableAuthorizedRadio(SmApplicationUtil
				.isDisableAuthorizedRadio(bean.getSelectedShareHolder(),
						bean.isValidSmUserLimit()));
		if (bean.isInDbAlreadyAuth()) {
			bean.setDisableAuthorizedRadio(false);
		}
	}

	/**
	 * Checks if is personnel view only.
	 * 
	 * @param smPersonnel
	 *            - SmPersonnel object to check
	 * @return true, if is personnel view only
	 */
	public static boolean isPersonnelViewOnly(SmPersonnel smPersonnel) {
		boolean retVal = false;
		if (smPersonnel.isPrevApprovedDirector()
				|| smPersonnel.isPrevApprovedEquityOwner()) {
			retVal = true;
		}
		return retVal;
	}

	/**
	 * Set or assign selected personnel nationality
	 * 
	 * @param SmApplicationManagedBean
	 *            bean
	 */
	public static void setSelectedPersonnelNationality(
			SmApplicationManagedBean bean) {
		if (bean.getSelectedShareHolder().getPmCountry().getCountryId() != null) {
			PmCountry cntry = new PmCountry();

			cntry = retrievePmCountryById(bean.getSelectedShareHolder()
					.getPmCountry().getCountryId());
			if (cntry != null) {
				bean.getSelectedShareHolder().setPmCountry(cntry);
				SmCommonUtil.setDefaultPersonnelNationality(bean
						.getSelectedShareHolder());
			}
		}
	}

	/**
	 * Set or assign selected personnel identification type
	 * 
	 * @param SmApplicationManagedBean
	 *            bean
	 */
	public static void setSelectedPersonnelIdentificationType(SmPersonnel per) {
		if (per.getIdentificationTypeId() != null) {
			per.setIdentificationType(SmApplicationUtil
					.retrievePmParameterById(per.getIdentificationTypeId()));
			if (per.getIdentificationType() == null) {
				per.setIdentificationType(new PmParameter());
			}
		} else {
			per.setIdentificationType(new PmParameter());
		}
	}

	/**
	 * Set or assign academic qualification
	 * 
	 * @param SmStaffAcademic
	 *            acad
	 */
	public static void assignQualification(SmStaffAcademic acad) {
		if (acad.getQualificationLevelId() != null) {
			acad.setQualificationId(SmApplicationUtil
					.retrievePmParameterById(acad.getQualificationLevelId()
							.longValue()));
			if (acad.getQualificationId() == null) {
				acad.setQualificationId(new PmParameter());
			}
		} else {
			acad.setQualificationId(new PmParameter());
		}
	}

	/**
	 * Get default registration status description by HQ type and bumi status
	 * 
	 * @param SmFacility
	 *            hq
	 * @param boolean isBumi
	 * @return PmStatusDesc
	 */
	public static PmStatusDesc getDefaultRegStatusDesc(SmFacility hq,
			boolean isBumi) {
		Long statusId = SupplierManagementConst.PM_STATUS_REGISTRATION_STATUS_REGISTERED;
		if (hq.getFacilityType().equals("H")) {
			if (isBumi) {
				statusId = SupplierManagementConst.PM_STATUS_REGISTRATION_STATUS_BUMI_FROM_HOME;
			} else {
				statusId = SupplierManagementConst.PM_STATUS_REGISTRATION_STATUS_NON_BUMI_FROM_HOME;
			}
		}
		return SmCommonUtil.getStatusDescByStatusId(statusId);
	}

	public static void initPoInquiry(SmApplicationManagedBean bean, boolean isNewInquiry) {
		// START 14530
		if (bean.getPoToSupplierInquiry() == null) {
			bean.setPoToSupplierInquiry(new SmApplInquiry());
		}
		// END 14530
		
		// #34392 - start
		bean.setInquirySiteVisitAfter(true);
		// #34392 - end

		if (!EntityHelper.isCollectionEmpty(bean.getSmAppl().getSmApplInquiries())) {

			// sort created date descendent
			Collections.sort(bean.getSmAppl().getSmApplInquiries(), new Comparator<SmApplInquiry>() {
				public int compare(SmApplInquiry o1, SmApplInquiry o2) {
					if (o1.getCreatedDate() == null || o2.getCreatedDate() == null)
						return 0;
					return o2.getCreatedDate().compareTo(o1.getCreatedDate());
				}
			});

			// if PO (Inquiry Section)
			if (bean.isPoUser()) {

				if (isNewInquiry) {
					bean.setPoToSupplierInquiry(new SmApplInquiry());
				} else {
					// set for inquiry histories
					bean.setPoSupplierInquiryHistory(new ArrayList<SmApplInquiry>());
					for (SmApplInquiry inquiry : bean.getSmAppl().getSmApplInquiries()) {
						if (!StringUtils.isBlank(inquiry.getInquirySubject()) && inquiry.getInquirySubject().equals(SupplierManagementConst.INQUIRY_PO_SUPPLIER)) {
							bean.getPoSupplierInquiryHistory().add(inquiry);
						}
					}
				}
				//#34392 - start
				if (bean.getCurrentStatus()
						.equals(SupplierManagementConst.PENDING_REVIEW_SITE_VISIT_REPORT_AFTER_APPROVAL)
						|| bean.getCurrentStatus()
								.equals(SupplierManagementConst.PENDING_REVIEW_SITE_VISIT_REPORT_AFTER_APPROVAL_BM)) {
					bean.setInquirySiteVisitAfter(false);
				}
				//#34392 - end
			}
			// if Supplier (Respond to PO inquiry)
			// START 14530
			else if (bean.isSupplierUser()) {

				if (bean.getPoToSupplierInquiry().getResponseDate() == null) {
					SmApplInquiry currentInquiry = bean.getSmAppl().getSmApplInquiries().get(0);
					bean.setPoToSupplierInquiry(currentInquiry);

					log.info("*** VALUE POTOSUPPLIERINQUIRY *** " + bean.getPoToSupplierInquiry().getInquiryText());

					bean.getPoToSupplierInquiry().setResponseText(StringUtils.EMPTY);
				}
			}

			// 14530
			else if (bean.isApproverUser()) {
				// set for inquiry histories
				bean.setPoSupplierInquiryHistory(new ArrayList<SmApplInquiry>());
				for (SmApplInquiry inquiry : bean.getSmAppl().getSmApplInquiries()) {
					if (!StringUtils.isBlank(inquiry.getInquirySubject()) && inquiry.getInquirySubject().equals(SupplierManagementConst.INQUIRY_PO_SUPPLIER)) {
						bean.getPoSupplierInquiryHistory().add(inquiry);
					}
				}
			}
		} else {
			// initialize po inquiry
			//#34392 - start
			bean.setPoSupplierInquiryHistory(new ArrayList<SmApplInquiry>());
			//#34392 - end
			bean.setPoToSupplierInquiry(new SmApplInquiry());
		}
	}

	private static void addRoles(List<SelectItem> roles, String epRole,
			boolean isAdmin) {
		SelectItem role = new SelectItem();
		if (isAdmin) {
			role.setLabel(getEpRoleDescByRoleCode(RoleCode.MOF_SUPPLIER_ADMIN
					.value()));
		} else {
			role.setLabel(getEpRoleDescByRoleCode(epRole));
		}
		role.setValue(epRole);
		roles.add(role);
	}

	/**
	 * Get eP Role description by role code
	 * 
	 * @param String
	 *            roleCode
	 * @return String
	 */
	private static String getEpRoleDescByRoleCode(String roleCode) {
		String retVal = "";
		RoleReqDTO reqDTO = new RoleReqDTO();
		reqDTO.setRoleCode(roleCode);
		reqDTO.setLanguage(new FacesScopes().getUserLocale().getLanguage());
		PmRoleDesc roleDesc = SmServiceLookup.getRoleSO()
				.retrieveRoleDescByRoleCode(reqDTO).getPmRoleDesc();
		if (roleDesc != null && StringUtils.isNotBlank(roleDesc.getRoleName())) {
			retVal = roleDesc.getRoleName();
		}
		return retVal;
	}

	/**
	 * To check whether need to disable ep role drop down
	 * 
	 * @param smPersonnel
	 * @return boolean
	 */
	public static boolean disableEpRoleDropDown(SmPersonnel smPersonnel,
			List<SmPersonnel> prevApprovedPersonnels) {
		boolean retVal = false;
		SmPersonnel prevPersonnel = getPrevApprovedPersonnel(smPersonnel,
				prevApprovedPersonnels);

		// disabled drop down for Admin
		if (!StringUtils.isBlank(smPersonnel.getEpRole())
				&& (smPersonnel.getEpRole().equals(
						RoleCode.MOF_SUPPLIER_ADMIN.value())
						|| smPersonnel.getEpRole().equals(
								RoleCode.SUPPLIER_TEMP.value()) || smPersonnel
						.getEpRole().equals(
								RoleCode.BASIC_SUPPLIER_ADMIN.value()))) {
			retVal = true;
		}
		// disabled drop down if softcert processing
		else if (prevPersonnel != null
				&& prevPersonnel.getSoftcertFlg() == SupplierManagementConst.SOFT_CERT_PROCESSING) {
			retVal = true;
		}
		return retVal;
	}

	/**
	 * Get previous approved personnel
	 * 
	 * @param currPersonnel
	 * @return SmPersonnel
	 */
	public static SmPersonnel getPrevApprovedPersonnel(
			SmPersonnel currPersonnel, List<SmPersonnel> prevApprovedPersonnels) {
		List<SmPersonnel> prevPersonnels = prevApprovedPersonnels;
		SmPersonnel retVal = null;

		if (currPersonnel != null
				&& !StringUtils.isBlank(currPersonnel.getIdentificationNo())
				&& !EntityHelper.isCollectionEmpty(prevPersonnels)) {
			for (SmPersonnel prevPersonnel : prevPersonnels) {
				if (prevPersonnel.getIdentificationNo().equals(
						currPersonnel.getIdentificationNo())) {
					retVal = prevPersonnel;
				}
			}
		}

		return retVal;
	}

	/**
	 * Getys Authorized Personnel Account Status
	 * 
	 * @param shareHolderBean
	 *            - SmPersonnel object
	 */
	public static String getPersonnelActivationStatus(SmPersonnel smPersonnel,
			SmSupplier smSupplier) {
		String accStatus = StringUtils.EMPTY;
		if (smSupplier != null && smSupplier.getSupplierId() != null
				&& smPersonnel.getEpRole() != null
				&& StringUtils.isNotBlank(smPersonnel.getEpRole())) {

			if (smPersonnel.getPersonnelId() != null
					&& SmApplicationUtil.isLatestApprovedPersonnelActivated(
							smPersonnel.getIdentificationNo(),
							smSupplier.getSupplierId())) {
				accStatus = SmCommonUtil.getMessage(
						"lbl.sm.activation.status.activated", null);
			} else {
				accStatus = SmCommonUtil.getMessage(
						"lbl.sm.activation.status.pending", null);
			}
		}
		return accStatus;
	}

	/**
	 * Validate personnel when perform deletion and return count selected
	 * authorized personnel for deletion
	 * 
	 * @param String
	 *            personType
	 * @param SmApplicationManagedBean
	 *            bean
	 * @return int
	 */
	public static int validateDeletePersonnel(String personType,
			SmApplicationManagedBean bean) {
		int countAuthorizedPersonnel = 0;
		boolean existAsBankSignatory = false;
		boolean existAsBranchPoc = false;
		bean.setPersonType(personType);
		for (SmPersonnel shr : bean.getSelectedShareHolders()) {
			//#29525-start
			if (shr.getPmUser() != null) {
				// #26050 - start
				if (shr.getEpRole() != null && PmCommonUtil.isAnyPendingTask(shr.getPmUser().getLoginId())) {
					throw new EpException(MessageConst.ERROR_770232);
				}
				// #26050 - end
			} //#29525-end
			if (shr.getEpRole() != null
					&& (shr.getEpRole().equalsIgnoreCase(
							RoleCode.SUPPLIER_TEMP.value())
							|| shr.getEpRole().equalsIgnoreCase(
									RoleCode.MOF_SUPPLIER_ADMIN.value()) || shr
							.getEpRole().equalsIgnoreCase(
									RoleCode.BASIC_SUPPLIER_ADMIN.value()))) {
				throw new EpException(MessageConst.ERROR_722553);
			}
			if (SmApplicationUtil.isPersonnelBankSignatory(shr, bean
					.getSmAppl().getSmSupplierBanks())) {
				existAsBankSignatory = true;
			}
			if (SmApplicationUtil.isPersonnelBranchPointOfContact(shr, bean
					.getSmAppl().getSmSupplierBranches())) {
				existAsBranchPoc = true;
			}

			if (existAsBankSignatory && existAsBranchPoc) {
				throw new EpException(MessageConst.ERROR_722588);
			} else {
				if (existAsBankSignatory) {
					throw new EpException(MessageConst.ERROR_722585);
				} else if (existAsBranchPoc) {
					throw new EpException(MessageConst.ERROR_722586);
				}
			}

			if (shr.getAuthorizedFlg() == DbValueConst.RECORD_STATUS_ACTIVE) {
				countAuthorizedPersonnel++;
			}
			//#29616 -start
			if (shr.getSoftcertFlg() == DbValueConst.RECORD_STATUS_ACTIVE) {
				throw new EpException(MessageConst.ERROR_722685,
						new String[] { shr.getName() });
			}//#29616 -end
		}
		return countAuthorizedPersonnel;
	}

	/**
	 * Assign selected SmPersonnel object to managed bean for editing the
	 * information.
	 * 
	 * @param shareHolderBean
	 *            - SmPersonnel object
	 */
	public static void assignSelShareHolder(SmPersonnel shareHolderBean,
			SmApplicationManagedBean bean, int currTab) {
		shareHolderBean.setBumiFlgTrans(shareHolderBean.getBumiFlg());
		shareHolderBean.setMuslimFlgTrans(shareHolderBean.getMuslimFlg());
		//#29315
		bean.setFlagMandatoryEpfNo(false);
		if (bean.getCurrentPageType().equalsIgnoreCase(
				SupplierManagementConst.SM_APPL_PAGE_TYPE_EQUITY)) {
			validatePaidCaptialAmt(false, bean, currTab);
			bean.setPersonnelInfoHeader(SmCommonUtil.getMessage(
					"lbl.sm.equity.owner.information", null));
			shareHolderBean.setEquityOwner(true);
		} else if (bean.getCurrentPageType().equalsIgnoreCase(
				SupplierManagementConst.SM_APPL_PAGE_TYPE_PERSONNEL)) {
			bean.setPersonnelInfoHeader(SmCommonUtil.getMessage(
					"lbl.sm.person.panHdr", null));
		}

		if (shareHolderBean.getPmUser() != null
				&& shareHolderBean.getPmUser().getUserId() != null
				&& shareHolderBean.getPmUser().getRecordStatusFlg() == DbValueConst.RECORD_STATUS_ACTIVE)
			bean.setDisableIcEmail(true);
		else
			bean.setDisableIcEmail(false);

		if (shareHolderBean.getEpRole() != null
				&& (shareHolderBean.getEpRole().equalsIgnoreCase(
						RoleCode.SUPPLIER_TEMP.value())
						|| shareHolderBean.getEpRole().equalsIgnoreCase(
								RoleCode.MOF_SUPPLIER_USER.value())
						|| shareHolderBean.getEpRole().equalsIgnoreCase(
								RoleCode.FL_USER.value())
						|| shareHolderBean.getEpRole().equalsIgnoreCase(
								RoleCode.MOF_SUPPLIER_ADMIN.value()) || shareHolderBean
						.getEpRole().equalsIgnoreCase(RoleCode.CM_USER.value()))) {
			bean.setUserWithRole(true);
		} else {
			bean.setUserWithRole(false);
		}
		bean.setSelectedShareHolder((SmPersonnel) SerializationUtils
				.clone(shareHolderBean));
		if (bean.getSelectedShareHolder().getPmCountry() == null) {
			bean.getSelectedShareHolder().setPmCountry(new PmCountry());
		}
		if (bean.getSelectedShareHolder().getPmRace() == null) {
			bean.getSelectedShareHolder().setPmRace(new PmRace());
		}
		if (bean.getSelectedShareHolder().getTitle() == null) {
			bean.getSelectedShareHolder().setTitle(new PmSalutation());
		}
		if (bean.getSelectedShareHolder().getEpRole() == null) {
			bean.getSelectedShareHolder().setEpRole("");
		}

		assignSelShareHolderAddress(bean.getSelectedShareHolder(), bean);
		managePersonnelRoles(bean, "Y");
		bean.setValidEquityLimit(SmApplicationUtil.manageEquityLimit(
				bean.getSelectedShareHolder(), bean.getBusinessTypeVal(),
				bean.getSmapplShareHolders(),bean.getSmSupplier()));//change by fahmi, log : 23858 13/03/2019 : add smSupplier
		if (bean.getSelectedShareHolder().getIdentificationTypeId() != null) {
			bean.getSelectedShareHolder().setIdentificationType(
					SmApplicationUtil
							.retrievePmParameterById(bean
									.getSelectedShareHolder()
									.getIdentificationTypeId()));
		}
		// start - #30064
		if (bean.getSelectedShareHolder().getIdentificationType() != null
				&& ParameterConst.PERMANENT_RESIDENCE_TYPES.equalsIgnoreCase(bean.getSelectedShareHolder()
						.getIdentificationType().getParameterCode())) {
			bean.getSelectedShareHolder().setIdentificationTypeId(
					SmApplicationUtil.retrievePmParameterByTypeAndCode(ParameterTypeConst.IDENTIFICATION_TYPES,
							ParameterConst.IDENTIFICATION_TYPES_IC).getParameterId());
		}
		// end - #30064
		if (bean.getSelectedShareHolder().getIdentificationType() == null) {
			bean.getSelectedShareHolder().setIdentificationType(
					new PmParameter());
		}
		bean.setRedirectToPersonPage(true);
		bean.setRemoveEpRole(false);

		// to determine whether user already authorize in DB
		boolean inDbAlreadyAuth = false;
		if (bean.getSelectedShareHolder().getPersonnelId() != null) {
			MofRegistrationReqDTO reqDTO = new MofRegistrationReqDTO();
			reqDTO.setApplId(bean.getSmAppl().getApplId());
			reqDTO.setPersonnel(bean.getSelectedShareHolder());
			inDbAlreadyAuth = SmServiceLookup.getMofRegistrationSO()
					.isPersonnelAuthorizedByIdNoApplId(reqDTO).isValid();
		}
		bean.setInDbAlreadyAuth(inDbAlreadyAuth);

		bean.setePRoleList(getPersonnelRoleItems(bean.getSelectedShareHolder(),
				bean.getSmAppl(), bean.getAuthVO()));

		if (bean.getSmAppl().getSupplierType()
				.equalsIgnoreCase(SupplierManagementConst.STYPE_CONSULTANT)
				&& !EntityHelper.isCollectionEmpty(bean.getePRoleList())
				&& (bean.getSelectedShareHolder().getEpRole() == null
						|| bean.getSelectedShareHolder().getEpRole().isEmpty() || bean
						.getSelectedShareHolder().getEpRole()
						.equalsIgnoreCase(RoleCode.MOF_SUPPLIER_USER.value()))) {
			bean.getSelectedShareHolder().setEpRole(
					RoleCode.MOF_SUPPLIER_USER.value());
		}

		// to determine whether shareholder for viewonly or edit
		bean.setShowPersonnelViewOnly(SmApplicationUtil
				.isPersonnelViewOnly(shareHolderBean));
		//#29086
		if (bean.isShowPersonnelViewOnly()|| bean.isDisAllowOwnerEdit()) {
			bean.setSelectedShareholderIdentificationType(SmCommonUtil
					.getParamDescriptionByLanguageAndPmParameter(bean
							.getSelectedShareHolder().getIdentificationType()));
		}

		// disable shareholder/personnel name and nationality if already
		// activate (have login id) or created in basic
		bean.setDisabledShareholderPersonnelNameAndNationality(isDisabledShareholderNameAndNationality(
				shareHolderBean, bean.getSmSupplier()));

		// disable or enable role selection
		bean.setDisableRoleSelection(disableEpRoleDropDown(shareHolderBean,
				bean.getPrevApprovedPersonnels()));

		// disable or enable authorized flag
		bean.setDisableAuthorizedRadio(isDisableAuthorizedRadio(
				shareHolderBean, bean.isValidSmUserLimit()));

		String epRole = shareHolderBean.getEpRole() == null ? StringUtils.EMPTY
				: shareHolderBean.getEpRole();
		if (bean.isInDbAlreadyAuth()
				&& !my.ep.constant.sm.SupplierManagementConst.SUPPLIER_ADMIN_ROLES
						.contains(epRole)) {
			bean.setDisableAuthorizedRadio(false);
		}

		// set previous selected personnel if exist
		bean.setSelectedPrevShareHolder((SmPersonnel) EntityHelper
				.getEntityBySinglePropertyFromList(
						shareHolderBean.getIdentificationNo(),
						bean.getPrevApprovedPersonnels(), "identificationNo"));
	}

	/**
	 * Checks the record status and Sets the disabled shareholder name and
	 * nationality.
	 * 
	 * @param smPersonnel
	 *            - SmPersonnel object to be checked or validated
	 */
	public static boolean isDisabledShareholderNameAndNationality(
			SmPersonnel smPersonnel, SmSupplier supplier) {
		boolean retVal = false;
		try {
			if (smPersonnel.getPmUser() != null
					&& smPersonnel.getPmUser().getRecordStatusFlg() == 1) {
				retVal = true;
			} else if (SmApplicationUtil.isPersonnelCreatedInBasic(smPersonnel,
					supplier.getSupplierId())) {
				retVal = true;
			}
			//START  #27230
			if(retVal){
				if(smPersonnel.isSsmData()){
					if(smPersonnel.getPmCountry() != null && smPersonnel.getPmCountry().getCountryId() != 246){
						retVal = true;
					}else{
						retVal = false;
					}
					//27821
					if(smPersonnel.getIdentificationType() != null && ParameterConst.IDENTIFICATION_TYPES_PASSPORT.equalsIgnoreCase(smPersonnel.getIdentificationType().getParameterCode())){
						retVal = false;
					}
					//29582,35300 start 
					if (smPersonnel.getIdentificationType() != null && smPersonnel.isPermanentResident() ){
						retVal = false;
					}//29582,35300 end 
					// #28831 - start
				} else {
					if (smPersonnel.getIdentificationType() != null
							&& ParameterConst.IDENTIFICATION_TYPES_IC.equalsIgnoreCase(smPersonnel.getIdentificationType().getParameterCode())
							&& (smPersonnel.getPmCountry() != null && smPersonnel.getPmCountry().getCountryId() != 131)
							&& !(smPersonnel.getSoftcertFlg() == SupplierManagementConst.IS_SOFTCERT_REQ || smPersonnel.getSoftcertFlg() == SupplierManagementConst.SOFT_CERT_PROCESSING)) {
						retVal = false;
					}
					//29582 start
					if (smPersonnel.getIdentificationType() != null
							&& ParameterConst.IDENTIFICATION_TYPES_PASSPORT.equalsIgnoreCase(smPersonnel
									.getIdentificationType().getParameterCode())) {
						retVal = false;
					}//29582 end
				}
				// #28831 - start
			}
			//END  #27230
		} catch (Exception ex) {
			log.error(ex.getMessage());
		}
		return retVal;
	}

	/**
	 * Manage personnel roles for the selected SmPersonnel in the application.
	 */
	//#30444 START--09052022--replace the whole method in pds with this method
	public static void managePersonnelRoles(SmApplicationManagedBean bean,
			String updateAddressDetails) {
		if (bean.getSmAppl().getSupplierType()
				.equalsIgnoreCase(SupplierManagementConst.STYPE_CONTRACTOR)
				&& !bean.isDisAllowOwnerEdit()) {
			//#30444 START--25082021--	
			if(bean.getBusinessTypeVal()
					.equalsIgnoreCase(SupplierManagementConst.SM_SUPPLIER_BUSINESS_TYPE_ROB_PARTNER_DEFAULT)
					&& !alreadyHadEquityOwnerAsStaff(bean.getSmAppl(), bean.getSelectedShareHolder().getPersonnelId())){
				if(bean.getSelectedShareHolder().isManagement()
						&& !bean.getSelectedShareHolder().isEquityOwner()) {
					bean.getSelectedShareHolder().setClerical(false);
					bean.getSelectedShareHolder().setDisableStaff(true);
				} else {
					bean.getSelectedShareHolder().setDisableStaff(false);
				}
				if(bean.getSelectedShareHolder().isEquityOwner()){
					bean.getSelectedShareHolder().setDisableStaff(false);
					bean.getSelectedShareHolder().setDisableManagement(false);
				}
			} else {
				if (bean.getSelectedShareHolder().isDirector()
						|| bean.getSelectedShareHolder().isHigherManagement()
						|| bean.getSelectedShareHolder().isEquityOwner()
						|| bean.getSelectedShareHolder().isManagement()){
					bean.getSelectedShareHolder().setClerical(false);
					bean.getSelectedShareHolder().setDisableStaff(true);
				}else {
					bean.getSelectedShareHolder().setDisableStaff(false);					
				}
			}
			//#30444 END--25082021--
	    } else if (bean.getSmAppl().getSupplierType()
				.equalsIgnoreCase(SupplierManagementConst.STYPE_CONSULTANT)
				&& !bean.isDisAllowOwnerEdit()) {
	    	//#30444 START--25082021--
	    	if (bean.getBusinessTypeVal()     
					.equalsIgnoreCase(SupplierManagementConst.SM_SUPPLIER_BUSINESS_TYPE_ROB_PARTNER_DEFAULT)
	    		&& !alreadyHadEquityOwnerAsStaff(bean.getSmAppl(), bean.getSelectedShareHolder().getPersonnelId())){
	    		//#30444 START--03062022--
	    		if(bean.getSelectedShareHolder().isSemiProf()){
					bean.getSelectedShareHolder().setProf(false);
					bean.getSelectedShareHolder().setManagement(false);
					bean.getSelectedShareHolder().setEquityOwner(false);
					bean.getSelectedShareHolder().setDisableStaff(true);
					bean.getSelectedShareHolder().setDisableProf(true);
					bean.getSelectedShareHolder().setDisableManagement(true);
					bean.getSelectedShareHolder().setDisableEquityOwner(true);
	    		}else{
					bean.getSelectedShareHolder().setDisableProf(false);
					bean.getSelectedShareHolder().setDisableManagement(false);
					bean.getSelectedShareHolder().setDisableEquityOwner(false);          
	    		}
	    		//#30444 END--03062022--
	    		if (!bean.getSelectedShareHolder().isEquityOwner()
						&& (bean.getSelectedShareHolder().isManagement()
						|| bean.getSelectedShareHolder().isProf()
						|| bean.getSelectedShareHolder().isSemiProf())) { //#30444 --03062022--
	    			bean.getSelectedShareHolder().setClerical(false); //#30444 --17022022--
					bean.getSelectedShareHolder().setDisableStaff(true);
	    		}else{
		    		bean.getSelectedShareHolder().setDisableStaff(false);
		    	}
	    		if (bean.getSelectedShareHolder().isEquityOwner()
						||bean.getSelectedShareHolder().isProf()
						||bean.getSelectedShareHolder().isManagement()){
					bean.getSelectedShareHolder().setSemiProf(false);
					bean.getSelectedShareHolder().setDisableSemiProf(true);
		    	}else{
		    		if(bean.getSelectedShareHolder().isClerical()){
		    			bean.getSelectedShareHolder().setSemiProf(false);
		    			bean.getSelectedShareHolder().setDisableSemiProf(true);
		    		}else{
		    			bean.getSelectedShareHolder().setDisableSemiProf(false);	
		    		}
		    	}
	    	} else {
				 if(bean.getSelectedShareHolder().isSemiProf()) {
						bean.getSelectedShareHolder().setClerical(false);
						bean.getSelectedShareHolder().setProf(false);
						bean.getSelectedShareHolder().setManagement(false);
						bean.getSelectedShareHolder().setEquityOwner(false);
						bean.getSelectedShareHolder().setHigherManagement(false); //#30444 --27032022--
						bean.getSelectedShareHolder().setDirector(false);  //#30444 --27032022--
						bean.getSelectedShareHolder().setDisableProf(true);
						bean.getSelectedShareHolder().setDisableManagement(true);
						bean.getSelectedShareHolder().setDisableEquityOwner(true);
						bean.getSelectedShareHolder().setDisableStaff(true);				
				   }else{
					   bean.getSelectedShareHolder().setDisableProf(false);
					   bean.getSelectedShareHolder().setDisableManagement(false);
					   bean.getSelectedShareHolder().setDisableEquityOwner(false);
					   bean.getSelectedShareHolder().setDisableStaff(false);
				   }
				// #38610-start
				if (bean.getSelectedShareHolder().isClerical()) {
					bean.getSelectedShareHolder().setProf(false);
					bean.getSelectedShareHolder().setManagement(false);
					bean.getSelectedShareHolder().setEquityOwner(false);
					bean.getSelectedShareHolder().setSemiProf(false);
					bean.getSelectedShareHolder().setHigherManagement(false);
					bean.getSelectedShareHolder().setDisableProf(true);
					bean.getSelectedShareHolder().setDisableManagement(true);
					bean.getSelectedShareHolder().setDisableEquityOwner(true);
					bean.getSelectedShareHolder().setDisableSemiProf(true);
				}// #38610-end
				 if(bean.getSelectedShareHolder().isEquityOwner()
							|| bean.getSelectedShareHolder().isManagement()
							|| bean.getSelectedShareHolder().isDirector()
							|| bean.getSelectedShareHolder().isProf()
							|| bean.getSelectedShareHolder().isSemiProf()
							|| bean.getSelectedShareHolder().isHigherManagement()){
		    			bean.getSelectedShareHolder().setClerical(false);
		    			bean.getSelectedShareHolder().setDisableStaff(true);
		    		} else{
		    			bean.getSelectedShareHolder().setDisableStaff(false);
					}
				 if (bean.getSelectedShareHolder().isEquityOwner()
							|| bean.getSelectedShareHolder().isManagement()
							|| bean.getSelectedShareHolder().isDirector()
							|| bean.getSelectedShareHolder().isProf()
							|| bean.getSelectedShareHolder().isClerical()
							|| bean.getSelectedShareHolder().isHigherManagement()){
		    			bean.getSelectedShareHolder().setSemiProf(false);
		    			bean.getSelectedShareHolder().setDisableSemiProf(true);
		    		} else{
		    			bean.getSelectedShareHolder().setDisableSemiProf(false);
					}
	    	}
	    }
		//#30444 END--25082021--
		if (bean.getSelectedShareHolder().getPersonnelId() != null
				&& updateAddressDetails.equalsIgnoreCase("Y")) {
			if (bean.getSelectedShareHolder().isEquityOwner()
					|| bean.getSelectedShareHolder().isDirector()
					|| bean.getSelectedShareHolder().isHigherManagement()) {
				bean.getShareHolderResdAddr().setSameAsPerm(false);
				bean.getShareHolderOffAddr().setSameAsPerm(false);
				assignSelShareHolderAddress(bean.getSelectedShareHolder(), bean);
			} else {
				bean.getShareHolderResdAddr().setSameAsPerm(true);
				bean.getShareHolderOffAddr().setSameAsPerm(true);
			}
		}
	}
	//#30444 END--09052022--
	private static boolean alreadyHadEquityOwnerAsStaff(SmAppl appl, Long personelId) { //#29666 - add variable Long 
		boolean retVal = false;
		for (SmPersonnel per : appl.getSmPersonnels()) {
			if (!per.getPersonnelId().equals(personelId)){  //#29666 - add condition
			if (per.isEquityOwner() && per.isClerical()) {
				retVal = true;
				break;
			}
		}
	 }
		return retVal;
	}

	/**
	 * Set address fields whether need to be mandatory
	 */
	public static void setRoleStaffAddrReqd(SmApplicationManagedBean bean) {
		bean.setRoleStaffAndPermAddrReqd(false);
		bean.setRoleStaffAndMailAddrReqd(false);
		if (bean.getShareHolderPermAddr() != null
				&& bean.getShareHolderPermAddr().getSmAddress() != null) {
			SmAddress permAddress = bean.getShareHolderPermAddr()
					.getSmAddress();
			bean.setRoleStaffAndPermAddrReqd(isAddressReqdForRoleStaff(
					bean.getSelectedShareHolder(), permAddress));
		}
		if (bean.getShareHolderMailAddr() != null
				&& bean.getShareHolderMailAddr().getSmAddress() != null) {
			SmAddress mailAddress = bean.getShareHolderMailAddr()
					.getSmAddress();
			bean.setRoleStaffAndMailAddrReqd(isAddressReqdForRoleStaff(
					bean.getSelectedShareHolder(), mailAddress));
		}
		
		if (bean.getSmAppl().getSupplierType()
				.equalsIgnoreCase(SupplierManagementConst.STYPE_CONSULTANT)) {
			populateMandatoryEpfNo(bean);
		}
	}

	/**
	 * Assign selected job function - SmStaffExperience object to managed bean
	 * for editing the information.
	 * 
	 * @param exp
	 *            - SmStaffExperience object
	 */
	public static void assignSelShrHldrJobFunc(SmStaffExperience exp,
			SmApplicationManagedBean bean) {
		bean.setSelectedJobFunction(exp);
		if (bean.getLatestPersonnel() != null) {
			bean.setLatestJobFunction((SmStaffExperience) EntityHelper
					.getEntityByMultiplePropertiesFromList(exp, bean
							.getLatestPersonnel().getSmStaffExperiences(),
							new String[] { "companyName", "toYear" }, null,
							null));
		}
		RequestContext.getCurrentInstance().execute(
				"equiJobFunctionInfo.show()");
		if (bean.isShowPersonnelViewOnly()) {
			SmCommonUtil.closeBlockUI();
		} else {
			assignValReqdToTrue(bean);
		}
	}

	/**
	 * Add or update selected job function info to SmStaffExperience list of the
	 * selected SmPersonnel.
	 */
	public static void addOrUpdateJobFunctions(SmApplicationManagedBean bean) {
		Long expCode = bean.getSelectedJobFunction().getStaffExpCode() == null ? getPersonnelMaxExpCode(bean
				.getSelectedShareHolder()) + 1 : bean.getSelectedJobFunction()
				.getStaffExpCode();
		if (!EntityHelper.isCollectionEmpty(bean.getSelectedShareHolder()
				.getSmStaffExperiences())) {
			StringBuilder sb1 = null;
			StringBuilder sb2 = null;
			for (SmStaffExperience exp : bean.getSelectedShareHolder()
					.getSmStaffExperiences()) {
				if (bean.getSelectedJobFunction() != null) {
					sb1 = new StringBuilder();
					sb1.append(bean.getSelectedJobFunction().getCompanyName());
					sb1.append(bean.getSelectedJobFunction().getDesignation());

					sb2 = new StringBuilder();
					sb2.append(exp.getCompanyName());
					sb2.append(exp.getDesignation());

					if (bean.getSelectedJobFunction().getStaffExpCode() == null
							|| (expCode != null
									&& exp.getStaffExpCode() != null && expCode
									.compareTo(exp.getStaffExpCode()) != 0)) {
						if (sb1.toString().equals(sb2.toString())
								&& bean.getSelectedJobFunction().getFromYear()
										.equals(exp.getFromYear())
								&& bean.getSelectedJobFunction().getToYear()
										.equals(exp.getToYear())) {
							throw new EpException(MessageConst.ERROR_722638,
									new String[] { exp.getCompanyName() });
						} else if (sb1.toString().equals(sb2.toString())
								&& (SmCommonUtil.isBetweenDuration(
										bean.getSelectedJobFunction()
												.getFromYear(), bean
												.getSelectedJobFunction()
												.getToYear(),
										exp.getFromYear(), exp.getToYear()))) {
							throw new EpException(MessageConst.ERROR_722639,
									new String[] { exp.getCompanyName() });
						}
					}
				}
			}
		}

		if (bean.getSelectedJobFunction().getFromYear() == null
				|| bean.getSelectedJobFunction().getToYear() == null) {
			throw new EpException(MessageConst.ERROR_722539);
		}

		if (bean.getSelectedJobFunction().getFromYear()
				.compareTo(bean.getSelectedJobFunction().getToYear()) > 0) {
			throw new EpException(MessageConst.ERROR_722531);

		}
		if (bean.getSelectedJobFunction().getStaffExpCode() == null) {
			bean.getSelectedJobFunction().setStaffExpCode(expCode);
		}

		SmStaffExperience existingExp = (SmStaffExperience) EntityHelper
				.getEntityBySinglePropertyFromList(expCode, bean
						.getSelectedShareHolder().getSmStaffExperiences(),
						"staffExpCode");
		if (existingExp == null) {
			bean.getSelectedShareHolder().getSmStaffExperiences()
					.add(bean.getSelectedJobFunction());
		} else {
			int i = 0;
			for (SmStaffExperience tmp : bean.getSelectedShareHolder()
					.getSmStaffExperiences()) {
				if (tmp.getStaffExpCode().equals(existingExp.getStaffExpCode())) {
					break;
				}
				i++;
			}
			bean.getSelectedShareHolder().getSmStaffExperiences()
					.set(i, bean.getSelectedJobFunction());
		}

		if (bean.getSelectedJobFunction().getStaffExperienceId() == null) {
			RequestContext.getCurrentInstance().execute(
					"suppPrsnJobFunDtWidVar.paginator.endLink.click()");
		}
		SmFieldChangeUtil.setPersonnelTableListChangeStatus(bean);

		RequestContext.getCurrentInstance().execute(
				"equiJobFunctionInfo.hide()");

	}

	/**
	 * Assign selected related company - SmRelatedCompany object to managed bean
	 * for editing the information.
	 * 
	 * @param com
	 *            -SmRelatedCompany object
	 */
	public static void assignSelShrHldrRelatedCompany(SmRelatedCompany com,
			SmApplicationManagedBean bean) {
		bean.setSelectedRelatedCompany(com);
		if (bean.getLatestPersonnel() != null) {
			bean.setLatestRelatedCompany((SmRelatedCompany) EntityHelper
					.getEntityByMultiplePropertiesFromList(com, bean
							.getLatestPersonnel().getSmRelatedCompanies(),
							new String[] { "regNo" }, null, null));
		}
		RequestContext.getCurrentInstance().execute("equiRelCompInfo.show()");
		if (bean.isShowPersonnelViewOnly()) {
			SmCommonUtil.closeBlockUI();
		} else {
			bean.setShowParentMsg(false);
			bean.setValueReqd(true);
		}
	}

	/**
	 * Add or update selected related company info to SmRelatedCompany list of
	 * the selected SmPersonnel.
	 */
	public static void addOrUpdateRelatedCompanies(SmApplicationManagedBean bean) {
		if (bean.getSelectedShareHolder().getSmRelatedCompanies().contains(bean.getSelectedRelatedCompany())) {

		} else {
			bean.getSelectedShareHolder().getSmRelatedCompanies().add(bean.getSelectedRelatedCompany());
			RequestContext.getCurrentInstance().execute("suppPrsnRelComDtWidVar.paginator.endLink.click()");
		}
		assignValReqdToFalse(bean);
		RequestContext.getCurrentInstance().execute("equiRelCompInfo.hide()");

	}

	/**
	 * Assign selected professional body - SmAssocication object to managed bean
	 * for editing the information.
	 * 
	 * @param asb
	 *            -SmAssociationBody object
	 */
	public static void assignSelShrHldrProfBody(SmAssociationBody asb,
			SmApplicationManagedBean bean) {
		bean.setSelectedStfPrfBdy(asb);
		if (bean.getLatestAppl() != null) {
			bean.setLatestStfPrfBdy((SmAssociationBody) EntityHelper
					.getEntityByMultiplePropertiesFromList(asb, bean
							.getLatestAppl().getSmAssociationBodies(),
							new String[] { "regNo" }, null, null));
		}
		RequestContext.getCurrentInstance().execute(
				"equiShrProfBeanInfo.show()");
		if (bean.isShowPersonnelViewOnly()) {
			SmCommonUtil.closeBlockUI();
		} else {
			assignValReqdToTrue(bean);
		}

	}

	/**
	 * Assign selected staff academics - SmStaffAcademic object to managed bean
	 * for editing the information.
	 * 
	 * @param aca
	 *            -SmStaffAcademic
	 */
	public static void assignSelShrHldrAcademics(SmStaffAcademic aca,
			SmApplicationManagedBean bean) {
		if (aca.getQualificationId() == null) {
			aca.setQualificationId(new PmParameter());
		}
		bean.setSelectedStaffAcademic(aca);
		if (bean.getLatestPersonnel() != null) {
			bean.setLatestStaffAcademic((SmStaffAcademic) EntityHelper
					.getEntityByMultiplePropertiesFromList(aca, bean
							.getLatestPersonnel().getSmStaffAcademics(), null,
							new String[] { "pmEduField", "pmEduLevel" }, null));
		}
		RequestContext.getCurrentInstance().execute("equiAcadInfo.show()");
		if (bean.isShowPersonnelViewOnly()) {
			SmCommonUtil.closeBlockUI();
		} else {
			assignValReqdToTrue(bean);
		}

	}

	/**
	 * Add or update selected prof body info to SmAssocationBody list of the
	 * selected SmPersonnel.
	 */
	public static void addOrUpdateShrHldrProfBody(SmApplicationManagedBean bean) {
		if (bean.getSelectedShareHolder().getSmAssociationBodies().contains(bean.getSelectedStfPrfBdy())) {

		} else {
			bean.getSelectedShareHolder().getSmAssociationBodies()
					.add(bean.getSelectedStfPrfBdy());
			RequestContext.getCurrentInstance().execute(
					"suppPrsnProfBdyDtWidVar.paginator.endLink.click()");
		}
		assignValReqdToFalse(bean);
		RequestContext.getCurrentInstance().execute(
				"equiShrProfBeanInfo.hide()");

	}

	/**
	 * Add or update selected staff academics info to SmStaffAcademic list of
	 * the selected SmPersonnel.
	 */
	public static void addOrUpdateShrHldrAcademic(SmApplicationManagedBean bean) {
		if (bean.getSelectedStaffAcademic().getQualificationLevelId() == null) {
			throw new EpException(SmCommonUtil.getMessage("lbl.sm.is.required",
					new String[] { SmCommonUtil.getMessage(
							"lbl.sm.qualification.level", null) }));
		}
		for (PmEduField edu : bean.getEduFieldList()) {
			if (bean.getSelectedStaffAcademic().getPmEduField().getEduFieldId()
					.equals(edu.getEduFieldId())) {
				bean.getSelectedStaffAcademic().setPmEduField(edu);
				break;
			}
		}

		if (bean.getSelectedStaffAcademic().getQualificationId() != null) {

			String qual = "";
			if (bean.getSelectedStaffAcademic().getQualificationId()
					.getParameterCode() != null) {
				qual = bean.getSelectedStaffAcademic().getQualificationId()
						.getParameterCode();
				if (bean.getSelectedShareHolder().isProf()) {
					if (qual.equalsIgnoreCase(SupplierManagementConst.SUPPLIER_EDU_NOTDEFINED)
							|| qual.equalsIgnoreCase(SupplierManagementConst.SUPPLIER_EDU_LEVEL_CERT)
							|| qual.equalsIgnoreCase(SupplierManagementConst.SUPPLIER_EDU_LEVEL_DIPLOMA)) {
						throw new EpException(MessageConst.ERROR_722574,
								new String[] { "Professional Staff",
										"BACHELOR'S DEGREE" });
					}
				} else if (bean.getSelectedShareHolder().isSemiProf()) {
					if (qual.equalsIgnoreCase(SupplierManagementConst.SUPPLIER_EDU_NOTDEFINED)
							|| qual.equalsIgnoreCase(SupplierManagementConst.SUPPLIER_EDU_LEVEL_CERT)) {
						throw new EpException(MessageConst.ERROR_722574,
								new String[] { "Semi-Professional Staff",
										"DIPLOMA" });
					}
				} else if (bean.getSelectedShareHolder().isClerical()) {
					if (qual.equalsIgnoreCase(SupplierManagementConst.SUPPLIER_EDU_NOTDEFINED)) {
						throw new EpException(
								MessageConst.ERROR_722574,
								new String[] { "Clerical Staff", "CERTIFICATE" });
					}
				}

			}
		}
		if (bean.getSelectedShareHolder().getSmStaffAcademics()
				.contains(bean.getSelectedStaffAcademic())) {
		} else {

			bean.getSelectedShareHolder().getSmStaffAcademics()
					.add(bean.getSelectedStaffAcademic());
			RequestContext.getCurrentInstance().execute(
					"suppPrsnAcadDtWidVar.paginator.endLink.click()");
		}
		assignValReqdToFalse(bean);
		RequestContext.getCurrentInstance().execute("equiAcadInfo.hide()");

	}

	/**
	 * Adds or update addresses information to the selected SmPersonnel
	 * 
	 * @param shareHolderBean
	 *            - SmPersonnel object
	 * @throws ParseException
	 *             the parse exception
	 */
	public static void addOrUpdateShareHolderAddress(
			SmPersonnel shareHolderBean, SmApplicationManagedBean bean)
			throws ParseException {

		// validate address if role is staff
		validateSaveAddressForRoleStaff(bean);

		if (shareHolderBean.getAuthorizedFlg() == 0) {
			bean.getShareHolderMailAddr().setSameAsPerm(true);
		}

		if (!shareHolderBean.isDirector()
				&& !shareHolderBean.isHigherManagement()) {
			bean.getShareHolderOffAddr().setSameAsPerm(true);
			bean.getShareHolderResdAddr().setSameAsPerm(true);
		}

		// clone the addrs start
		cloneShareHolderAddress(bean);
		// clone the addrs end

		if (shareHolderBean.getSmPersonnelAddresses().isEmpty()) {
			processAddNewAddress(shareHolderBean, bean.getShareHolderPermAddr());
			processAddNewAddress(shareHolderBean, bean.getShareHolderMailAddr());
			processAddNewAddress(shareHolderBean, bean.getShareHolderOffAddr());
			processAddNewAddress(shareHolderBean, bean.getShareHolderResdAddr());
		} else {

			for (SmPersonnelAddress addr : shareHolderBean
					.getSmPersonnelAddresses()) {
				processUpdatedAddressList(addr, shareHolderBean, bean);
			}
			processUpdatedAddress(shareHolderBean,
					bean.getShareHolderPermAddr());
			processUpdatedAddress(shareHolderBean,
					bean.getShareHolderMailAddr());
			processUpdatedAddress(shareHolderBean, bean.getShareHolderOffAddr());
			processUpdatedAddress(shareHolderBean,
					bean.getShareHolderResdAddr());
		}
		bean.setSelectedShareHolder(shareHolderBean);

	}

	/**
	 * Add or create a new sm personnel child object
	 */
	public static void addNewPersonnelChildObject(
			SmApplicationManagedBean bean, SmPersonnel selectedPersonnel,
			Object childObject, String beanPropName, String addPopupName) {
		try {
			if (childObject == null) {
				childObject = new Object();
			}
			PropertyUtils.setProperty(childObject, "smPersonnel",
					selectedPersonnel);
			PropertyUtils.setProperty(bean, beanPropName, childObject);
			assignValReqdToTrue(bean);
			RequestContext.getCurrentInstance().execute(
					addPopupName + ".show()");
		} catch (IllegalAccessException e) {
			log.error(e.getMessage(), e);
		} catch (InvocationTargetException e) {
			log.error(e.getMessage(), e);
		} catch (NoSuchMethodException e) {
			log.error(e.getMessage(), e);
		}
	}

	/**
	 * Delete sm personnel child object
	 */
	public static void deletePersonnelChildObject(
			SmApplicationManagedBean bean, SmPersonnel selectedPersonnel,
			String selectedListPropName, String mainListPropName,
			String exceptionId, String[] exceptionParam) {
		try {
			List<? extends Object> selectedListToDelete = getPropListValue(
					bean, selectedListPropName);
			List<? extends Object> mainListToDelete = getPropListValue(
					selectedPersonnel, mainListPropName);
			if (!EntityHelper.isCollectionEmpty(selectedListToDelete)) {
				for (Object deleteObj : selectedListToDelete) {
					mainListToDelete.remove(deleteObj);
				}
				selectedListToDelete.clear();
			} else {
				if (exceptionParam != null && exceptionParam.length > 0) {
					throw new EpException(exceptionId, exceptionParam);
				} else {
					throw new EpException(exceptionId);
				}

			}
		} catch (IllegalAccessException e) {
			log.error(e.getMessage(), e);
		} catch (InvocationTargetException e) {
			log.error(e.getMessage(), e);
		} catch (NoSuchMethodException e) {
			log.error(e.getMessage(), e);
		}
	}

	/**
	 * Get years select item list for work experience
	 * 
	 * @return List<SelectItem>
	 */
	public static List<SelectItem> getYearsWorkExp(
			SmStaffExperience selectedExp, List<SelectItem> mainYearList) {
		List<SelectItem> initYears = mainYearList;
		List<SelectItem> selectYears = new ArrayList<SelectItem>();
		if (initYears != null && selectedExp != null
				&& selectedExp.getFromYear() != null
				&& selectedExp.getFromYear().compareTo(0) != 0) {
			int minYear = Integer
					.parseInt(selectedExp.getFromYear().toString());

			Iterator<SelectItem> itemIt = initYears.iterator();
			while (itemIt.hasNext()) {
				SelectItem item = itemIt.next();
				if (item.getValue() != null) {
					int tmp = Integer.parseInt(item.getValue().toString());
					if (tmp >= minYear) {
						selectYears.add(item);
					}
				} else {
					selectYears.add(item);
				}
			}
		}
		return selectYears;
	}

	/**
	 * Get personnel maximum experience code
	 * 
	 * @param SmPersonnel
	 *            selectedPersonnel
	 * @return Long
	 */
	public static Long getPersonnelMaxExpCode(SmPersonnel selectedPersonnel) {
		Long expCode = 0L;
		if (!EntityHelper.isCollectionEmpty(selectedPersonnel
				.getSmStaffExperiences())) {
			for (SmStaffExperience exp : selectedPersonnel
					.getSmStaffExperiences()) {
				if (exp.getStaffExpCode() != null
						&& exp.getStaffExpCode().compareTo(expCode) > 0) {
					expCode = exp.getStaffExpCode();
				}
			}
		}
		return expCode;
	}

	/**
	 * Get list of academic qualification
	 * 
	 * @return List<SelectItem>
	 */
	public static List<SelectItem> getQualificationLevels(
			SmApplicationManagedBean bean) {
		String suppType = StringUtils.isNotBlank(bean.getNewSupplierType()) ? bean
				.getNewSupplierType() : bean.getSmAppl().getSupplierType();
		List<SelectItem> finalList = new ArrayList<SelectItem>();
		SelectItem item = null;

		if (bean.getSelectedShareHolder() != null
				&& StringUtils.isNotBlank(suppType)
				&& suppType.equals("J")
				&& (bean.getSelectedShareHolder().isEquityOwner()
						|| bean.getSelectedShareHolder().isProf()
						|| bean.getSelectedShareHolder().isSemiProf() || bean
						.getSelectedShareHolder().isClerical())) {
			for (PmParameterDesc eduLevel : bean.getEduLevelList()) {
				if ((bean.getSelectedShareHolder().isEquityOwner() && SupplierManagementConst.EQUI_OWNER_EDU_LEVEL_CODES
						.contains(eduLevel.getPmParameter().getParameterCode()))
						|| (bean.getSelectedShareHolder().isProf() && SupplierManagementConst.PROF_STAFF_EDU_LEVEL_CODES
								.contains(eduLevel.getPmParameter()
										.getParameterCode()))
						|| (bean.getSelectedShareHolder().isSemiProf() && SupplierManagementConst.SEMI_PROF_STAFF_EDU_LEVEL_CODES
								.contains(eduLevel.getPmParameter()
										.getParameterCode()))
						|| (bean.getSelectedShareHolder().isClerical() && SupplierManagementConst.CLERICAL_STAFF_EDU_LEVEL_CODES
								.contains(eduLevel.getPmParameter()
										.getParameterCode()))) {
					item = (SelectItem) EntityHelper
							.getEntityBySinglePropertyFromList(
									eduLevel.getParameterDescId(), finalList,
									"value");
					if (item == null) {
						item = new SelectItem();
						item.setLabel(eduLevel.getCodeName());
						item.setValue(eduLevel.getPmParameter()
								.getParameterId());
						finalList.add(item);
					}
				}
			}

		} else {
			finalList = getAllPmEduLevelItems(finalList, bean);
		}
		return finalList;
	}

	/**
	 * Count current authorize personnel in application personnels
	 * 
	 * @return int
	 */
	public static int getCurrAuthPersonnelCount(SmApplicationManagedBean bean) {
		int count = 0;
		for (SmPersonnel per : bean.getSmAppl().getSmPersonnels()) {
			if ((per.getRecordStatusFlg() == DbValueConst.RECORD_STATUS_ACTIVE || per
					.getRecordStatusFlg() == DbValueConst.RECORD_STATUS_PENDING_ACTIVATION)
					&& per.getAuthorizedFlg() == DbValueConst.RECORD_STATUS_ACTIVE) {
				count++;
			}
		}
		return count;
	}

	public static List<SmSupplierCategory> getToRemoveConsultantCategoriesByChangeCategorType(
			SmAppl smAppl, List<SmSupplierCategory> activeCategoryList,
			String physicalConsulting) {
		List<SmSupplierCategory> catList = new ArrayList<SmSupplierCategory>();
		if (!StringUtils.isBlank(smAppl.getSupplierType())
				&& smAppl.getSupplierType().equals("J")) {

			// Remove from active list
			if (physicalConsulting
					.equals(my.ep.constant.sm.SupplierManagementConst.PHYSICAL_SERVICES_CAT_CODE)) {
				for (SmSupplierCategory cat : activeCategoryList) {
					if (cat.getPmCategoryL1()
							.getCategoryL1Code()
							.equals(my.ep.constant.sm.SupplierManagementConst.NON_PHYSICAL_SERVICES_CAT_CODE)
							&& cat.getRecordStatusFlg().compareTo(
									SupplierManagementConst.PENDING_ACT_STATUS) == 0) {
						catList.add(cat);
					}

				}
			} else {
				for (SmSupplierCategory cat : activeCategoryList) {
					if (cat.getPmCategoryL1()
							.getCategoryL1Code()
							.equals(my.ep.constant.sm.SupplierManagementConst.PHYSICAL_SERVICES_CAT_CODE)
							&& cat.getRecordStatusFlg().compareTo(
									SupplierManagementConst.PENDING_ACT_STATUS) == 0) {
						catList.add(cat);
					}

				}
			}
			// Remove from main list
			if (physicalConsulting
					.equals(my.ep.constant.sm.SupplierManagementConst.PHYSICAL_SERVICES_CAT_CODE)) {
				for (SmSupplierCategory cat : smAppl.getSmSupplierCategories()) {
					if (cat.getPmCategoryL1()
							.getCategoryL1Code()
							.equals(my.ep.constant.sm.SupplierManagementConst.NON_PHYSICAL_SERVICES_CAT_CODE)
							&& cat.getRecordStatusFlg().compareTo(
									SupplierManagementConst.PENDING_ACT_STATUS) == 0) {
						catList.add(cat);
					}

				}
			} else {
				for (SmSupplierCategory cat : smAppl.getSmSupplierCategories()) {
					if (cat.getPmCategoryL1()
							.getCategoryL1Code()
							.equals(my.ep.constant.sm.SupplierManagementConst.PHYSICAL_SERVICES_CAT_CODE)
							&& cat.getRecordStatusFlg().compareTo(
									SupplierManagementConst.PENDING_ACT_STATUS) == 0) {
						catList.add(cat);
					}
				}
			}
		}
		return catList;
	}

	/**
	 * Validation for saving address for role staff
	 */
	private static void validateSaveAddressForRoleStaff(SmApplicationManagedBean bean) {
		StringBuilder sb = new StringBuilder();

		if (bean.getShareHolderPermAddr().getSmAddress() != null) {
			SmAddress permAddr = bean.getShareHolderPermAddr().getSmAddress();
			if (isAddressReqdForRoleStaff(bean.getSelectedShareHolder(), permAddr)) {
				constructSaveStaffRoleAddressErrMsg(permAddr, "lbl.sm.person.permAddr", sb);
			}
		}

		if (bean.getShareHolderMailAddr() != null && bean.getShareHolderMailAddr().getSmAddress() != null) {
			SmAddress mailAddr = bean.getShareHolderMailAddr().getSmAddress();
			if (isAddressReqdForRoleStaff(bean.getSelectedShareHolder(), mailAddr)) {
				constructSaveStaffRoleAddressErrMsg(mailAddr, "lbl.sm.person.mailAddr", sb);
			}
		}

		if (sb.length() > 0) {
			throw new EpException(sb.toString());
		}
	}

	/**
	 * Construct validation message for saving address for role staff
	 * 
	 * @param smAddress
	 * @param addressTypeKey
	 * @param sb
	 */
	private static void constructSaveStaffRoleAddressErrMsg(SmAddress smAddress, String addressTypeKey, StringBuilder sb) {

		if (StringUtils.isBlank(smAddress.getAddress1())) {
			sb.append(getAddressFieldRequiredMsg(addressTypeKey, "lbl.sm.address")).append("<br />");
		}
		if (smAddress.getPmState() == null || smAddress.getPmState().getStateId() == null) {
			sb.append(getAddressFieldRequiredMsg(addressTypeKey, "lbl.sm.state")).append("<br />");
		}
		if (smAddress.getPmDistrict() == null || smAddress.getPmDistrict().getDistrictId() == null) {
			sb.append(getAddressFieldRequiredMsg(addressTypeKey, "lbl.sm.district")).append("<br />");
		}
		if (smAddress.getPmCity() == null || smAddress.getPmCity().getCityId() == null) {
			sb.append(getAddressFieldRequiredMsg(addressTypeKey, "lbl.sm.towncity")).append("<br />");
		}
		if (StringUtils.isBlank(smAddress.getPostcode())) {
			sb.append(getAddressFieldRequiredMsg(addressTypeKey, "lbl.sm.postcode")).append("<br />");
		}
	}

	/**
	 * Get address required msg by address type and address field
	 * 
	 * @param String
	 *            addrTypeKey
	 * @param String
	 *            addrFieldKey
	 * @return String
	 */
	private static String getAddressFieldRequiredMsg(String addrTypeKey, String addrFieldKey) {
		return SmCommonUtil.getMessage("lbl.sm.required.msg", new String[] { 
				SmCommonUtil.getMessage(addrTypeKey, null) + " - " + SmCommonUtil.getMessage(addrFieldKey, null) 
				}
		);
	}

	private static List<SelectItem> getAllPmEduLevelItems(
			List<SelectItem> eduLevelItems, SmApplicationManagedBean bean) {
		SelectItem item = null;
		for (PmParameterDesc eduLevel : bean.getEduLevelList()) {
			item = (SelectItem) EntityHelper.getEntityBySinglePropertyFromList(
					eduLevel.getParameterDescId(), eduLevelItems, "value");
			if (item == null) {
				item = new SelectItem();
				item.setLabel(eduLevel.getCodeName());
				item.setValue(eduLevel.getPmParameter().getParameterId()); //#36360
				eduLevelItems.add(item);
			}
		}
		return eduLevelItems;
	}

	@SuppressWarnings("unchecked")
	private static List<? extends Object> getPropListValue(Object obj,
			String mainListPropName) throws IllegalAccessException,
			InvocationTargetException, NoSuchMethodException {
		return (List<? extends Object>) PropertyUtils.getProperty(obj,
				mainListPropName);
	}

	/**
	 * Assign value reqd to false when anyof the pop up is opened in the
	 * screen.Used for enable and disable screen validation dynamically.
	 */
	public static void assignValReqdToTrue(SmApplicationManagedBean bean) {
		bean.setShowParentMsg(false);
		bean.setValueReqd(true);
	}

	/**
	 * Assign value reqd to false when anyof the pop up is closed in the
	 * screen.Used for enable and disable screen validation dynamically.
	 */
	public static void assignValReqdToFalse(SmApplicationManagedBean bean) {
		bean.setShowParentMsg(true);
		bean.setValueReqd(false);
	}

	/**
	 * Check whether the rejected application still valid for resubmission
	 * compare to no. of days allowed to resubmit after rejected
	 * 
	 * @param SmAppl
	 *            rejAppl
	 * @return boolean
	 */
	public static boolean isRejectedApplValidForResubmission(SmAppl rejAppl) {
		boolean valid = true;
		List<Long> statusIds = new ArrayList<Long>();
		statusIds.add(Long.parseLong(SupplierManagementConst.PM_STATUS_REJECTED_BY_PROCESSING_OFFICER));
		statusIds.add(Long.parseLong(SupplierManagementConst.PM_STATUS_REJECTED_BY_APPROVER));
		//#32627 START
		statusIds.add(Long.parseLong(SupplierManagementConst.PM_STATUS_REJECTED_BY_SYSTEM));
		statusIds.add(Long.parseLong(SupplierManagementConst.PM_STATUS_REJECTED_BY_SYSTEM_SDO_INQUIRY));
		statusIds.add(Long.parseLong(SupplierManagementConst.PM_STATUS_REJECTED_BY_SYSTEM_PO_INQUIRY));
		statusIds.add(SupplierManagementConst.PM_STATUS_AUTOREJECT_UNSUBMIT_OFFLINE_DOCUMENT);
		//#32627 END
		SmWorkFlowStatusReqDTO swReqDTO = new SmWorkFlowStatusReqDTO();
		swReqDTO.setStatusIds(statusIds);
		swReqDTO.setApplId(rejAppl.getApplId());
		SmWorkflowStatus currWorkflow = SmServiceLookup.getSmWorkFlowStatusSO().retrieveWorkflowByDocIdAndMultipleStatus(swReqDTO).getWorkflowStatus();
		if (currWorkflow != null) {
			Timestamp currentDate = new Timestamp(System.currentTimeMillis());
			long diff = currentDate.getTime() - currWorkflow.getCreatedDate().getTime();
			long diffDays = diff / (24 * 60 * 60 * 1000);
			if (diffDays >= SmRuleHelper.getInstance().getApplicationResubmissionDays()) {
				valid = false;
			}
		}
		return valid;
	}

	/**
	 * Get the payment mode i.e. to determine if payment required.
	 * 
	 * @return true, if successful
	 */
	public static boolean getPaymentMode(SmAppl smAppl) {
		boolean payReq = false;
		if (smAppl.getApplType().equalsIgnoreCase(
				ParameterConst.NEW_SM_APPLN_FOR_RENEW)
				|| smAppl.getApplType().equalsIgnoreCase(
						ParameterConst.NEW_SM_APPLN_FOR_MOF)) {
			payReq = true;
		}
		return payReq;
	}

	/**
	 * Wild card search for rejection reasons
	 * 
	 * @param String
	 *            searchQuery
	 * @param List
	 *            <PmReasonDesc> rejectReasons
	 * @return List<PmReasonDesc>
	 */
	public static List<PmReasonDesc> getWildCardSearchRejectReason(
			String searchQuery, List<PmReasonDesc> rejectReasons) {
		List<PmReasonDesc> resultList = new ArrayList<PmReasonDesc>();
		if (!EntityHelper.isCollectionEmpty(rejectReasons)) {
			if (StringUtils.isBlank(searchQuery)) {
				resultList = rejectReasons;
			} else {
				//#37896 START
				for (PmReasonDesc tmp : rejectReasons) {					
					String stringtest = tmp.getReasonDesc().replaceAll("\\s{2,}"," ").trim();

					if (StringUtils.upperCase(stringtest).contains(
							StringUtils.upperCase(searchQuery))) {
						resultList.add(tmp);
					}
				}
				//#37896 END
			}
		}
		return resultList;
	}

	/**
	 * Get whether address fields is mandatory for role staff
	 * 
	 * @param smAddress
	 * @return boolean
	 */
	private static boolean isAddressReqdForRoleStaff(SmPersonnel personnel,
			SmAddress smAddress) {
		boolean retVal = false;
		if (personnel.isClerical()) {
			if (StringUtils.isNotBlank(smAddress.getAddress1())) {
				retVal = true;
			} else if (smAddress.getPmState() != null
					&& smAddress.getPmState().getStateId() != null) {
				retVal = true;
			} else if (smAddress.getPmDistrict() != null
					&& smAddress.getPmDistrict().getDistrictId() != null) {
				retVal = true;
			} else if (smAddress.getPmCity() != null
					&& smAddress.getPmCity().getCityId() != null) {
				retVal = true;
			} else if (StringUtils.isNotBlank(smAddress.getPostcode())) {
				retVal = true;
			}
		}
		return retVal;
	}

	/**
	 * Check whether address need to be saved
	 * 
	 * @param smAddress
	 * @return
	 */
	private static boolean isNoNeedToSaveAddress(SmAddress smAddress) {
		boolean retVal = false;
		if (StringUtils.isBlank(smAddress.getAddress1())
				&& (smAddress.getPmState() == null || smAddress.getPmState()
						.getStateId() == null)
				&& (smAddress.getPmDistrict() == null || smAddress
						.getPmDistrict().getDistrictId() == null)
				&& (smAddress.getPmCity() == null || smAddress.getPmCity()
						.getCityId() == null)
				&& StringUtils.isBlank(smAddress.getPostcode())) {
			retVal = true;
		}
		return retVal;
	}

	/**
	 * Add new address for personnel
	 * 
	 * @param SmPersonnel
	 *            shareHolderBean
	 * @param SmPersonnelAddress
	 *            smPersonnelAddress
	 */
	private static void processAddNewAddress(SmPersonnel shareHolderBean,
			SmPersonnelAddress smPersonnelAddress) {
		if (!isNoNeedToSaveAddress(smPersonnelAddress.getSmAddress())) {
			shareHolderBean.getSmPersonnelAddresses().add(smPersonnelAddress);
		}
	}

	/**
	 * Update edited address or remove if is not needed for personnel
	 * 
	 * @param SmPersonnel
	 *            shareHolderBean
	 * @param SmPersonnelAddress
	 *            addr
	 */
	private static void processUpdatedAddress(SmPersonnel shareHolderBean,
			SmPersonnelAddress addr) {
		if (!shareHolderBean.getSmPersonnelAddresses().contains(addr)
				&& !isNoNeedToSaveAddress(addr.getSmAddress())) {
			shareHolderBean.getSmPersonnelAddresses().add(addr);
		} else {
			if (shareHolderBean.getSmPersonnelAddresses().contains(addr)
					&& isNoNeedToSaveAddress(addr.getSmAddress())) {
				shareHolderBean.getSmPersonnelAddresses().remove(addr);
			}
		}
	}

	/**
	 * Update personnel address list and if not exist add to the list, if not
	 * needed remove from list
	 * 
	 * @param SmPersonnelAddress
	 *            addr
	 * @param SmPersonnel
	 *            shareHolderBean
	 */
	private static void processUpdatedAddressList(SmPersonnelAddress addr,
			SmPersonnel shareHolderBean, SmApplicationManagedBean bean) {
		if (addr.getAddressType().equalsIgnoreCase(
				SupplierManagementConst.SM_PERSONNEL_ADDR_TYPE_MAIL)) {
			if (!isNoNeedToSaveAddress(addr.getSmAddress())) {
				bean.setShareHolderMailAddr(addr);
			}
		}
		if (addr.getAddressType().equalsIgnoreCase(
				SupplierManagementConst.SM_PERSONNEL_ADDR_TYPE_OFFICE)) {
			if (!isNoNeedToSaveAddress(addr.getSmAddress())) {
				bean.setShareHolderOffAddr(addr);
			}
		}
		if (addr.getAddressType().equalsIgnoreCase(
				SupplierManagementConst.SM_PERSONNEL_ADDR_TYPE_RES)) {
			if (!isNoNeedToSaveAddress(addr.getSmAddress())) {
				bean.setShareHolderResdAddr(addr);
			}
		}
		if (addr.getAddressType().equalsIgnoreCase(
				SupplierManagementConst.SM_PERSONNEL_ADDR_TYPE_PERM)
				&& !isNoNeedToSaveAddress(addr.getSmAddress())) {
			if (!isNoNeedToSaveAddress(addr.getSmAddress())) {
				bean.setShareHolderPermAddr(addr);
			}
		}
	}

	public static void setSupplierCategoryEffectiveDate(SmSupplierCategory cat,
			String applType) {
		Calendar c = Calendar.getInstance();
		if (cat.getRecordStatusFlg() == DbValueConst.RECORD_STATUS_ACTIVE
				&& cat.getCategoryEffDate() == null) {
			if (StringUtils.isNotBlank(applType)
					&& !applType.equals(ParameterConst.NEW_SM_APPLN_FOR_MOF)
					&& !applType.equals(ParameterConst.NEW_SM_APPLN_FOR_RENEW)
					&& !applType.equals(ParameterConst.NEW_SM_APPLN_FOR_CANCEL)
					&& !applType
							.equals(ParameterConst.NEW_SM_APPLN_FOR_DELETE_PENDING_APPL)) {
				c.add(Calendar.MONTH, SmServiceLookup.getSmRuleSO()
						.retrieveNewCatEffectiveMonthAfterAppoved()
						.getNewCatEffectiveMonth());
			}
			cat.setCategoryEffDate(c.getTime());
			// #30929 start
			log.info("<<< Updating SmSupplierCategory.CategoryEffDate >>>");
			log.info("<<< SmSupplierCategory.CategoryEffDate value : " + cat.getCategoryEffDate() + " >>>");
			// #30929 end
		}
	}

	/**
	 * Set SME status and application registration status
	 * 
	 * @param SmAppl
	 *            smAppl
	 * @param Long
	 *            approveStatusId
	 */
	public static PmRegStatus setApplicationApproveStatus(SmAppl smAppl,
			Long approveStatusId) {
		// approve SME Status
		SmApplicationUtil.setSmeStatus(smAppl);

		PmRegStatus regStatus = null;
		if (approveStatusId != null) {
			PmStatus status = SmCommonUtil.getInstance().retrievePmStatusById(
					approveStatusId);
			regStatus = status.getPmRegStatus();
		} else {
			SmApplSectionReview poRev = (SmApplSectionReview) EntityHelper
					.getEntityBySinglePropertyFromList(
							SupplierManagementConst.SECTION_REV_ROLE_PO,
							smAppl.getSmApplSectionReviews(), "roleType");
			if (poRev != null && poRev.getPmRegStatus() != null) {
				regStatus = poRev.getPmRegStatus();
			}
		}

		if (regStatus != null) {
			smAppl.setPmStatus(regStatus.getPmStatus());
			smAppl.setPmRegStatus(regStatus);
			if (smAppl.getApplType() != null
					&& smAppl.getApplType().equals(
							ParameterConst.NEW_SM_APPLN_FOR_BUMI)) {
				if (!regStatus.isBumiAppl()) {
					throw new EpException(MessageConst.ERROR_722671);
				}
				smAppl.getSmSupplier().setBumi(true);
				smAppl.setBumi(true);
			} else {
				smAppl.getSmSupplier().setBumi(regStatus.isBumiAppl());
				smAppl.setBumi(smAppl.getSmSupplier().isBumi());
			}
		}
		return regStatus;
	}

	public static void setCategoryNameForApprovedCategory(SmSupplierCategory cat) {
		CategoryCodeReqDTO reqDTO = null;
		if (cat.getPmCategoryL1() != null
				&& cat.getPmCategoryL1().getCategoryL1Id() != null) {
			reqDTO = new CategoryCodeReqDTO();
			reqDTO.setCategoryL1Id(cat.getPmCategoryL1().getCategoryL1Id());
			PmCategoryL1 l1 = SmServiceLookup.getCategoryCodeSO()
					.retrieveCategoryL1DtlById(reqDTO).getCategoryL1();
			if (l1 != null) {
				cat.setCategoryL1Name(l1.getCategoryName());
			}
		}
		if (cat.getPmCategoryL2() != null
				&& cat.getPmCategoryL2().getCategoryL2Id() != null) {
			reqDTO = new CategoryCodeReqDTO();
			reqDTO.setCategoryL2Id(cat.getPmCategoryL2().getCategoryL2Id());
			PmCategoryL2 l2 = SmServiceLookup.getCategoryCodeSO()
					.retrieveCategoryL2DtlById(reqDTO).getCategoryL2();
			if (l2 != null) {
				cat.setCategoryL2Name(l2.getCategoryName());
			}
		}
		if (cat.getPmCategoryL3() != null
				&& cat.getPmCategoryL3().getCategoryL3Id() != null) {
			reqDTO = new CategoryCodeReqDTO();
			reqDTO.setCategoryL3Id(cat.getPmCategoryL3().getCategoryL3Id());
			PmCategoryL3 l3 = SmServiceLookup.getCategoryCodeSO()
					.retrieveCategoryL3DtlById(reqDTO).getCategoryL3();
			if (l3 != null) {
				cat.setCategoryL3Name(l3.getCategoryName());
			}
		}
		if (cat.getApplRefId() == null) {
			cat.setApplRefId(cat.getSmAppl().getApplId());
		}
	}

	/**
	 * Sets the default values for new added personnel.
	 * 
	 * @param shr
	 *            - SmPersonnel object
	 */
	private static void setDefaultPersonnelValues(SmPersonnel shr, SmAppl smAppl) {
		shr.setTitle(new PmSalutation());
		shr.setRevNo(1);
		shr.setSmAppl(smAppl);
		shr.setBumiFlg(1);
		shr.setMuslimFlg(1);
		shr.setPmCountry(new PmCountry());
		shr.setPmRace(new PmRace());
		shr.setIdentificationType(new PmParameter());
		shr.setSmPersonnelAddresses(new ArrayList<SmPersonnelAddress>());
		shr.setSmStaffAcademics(new ArrayList<SmStaffAcademic>());
		shr.setSmStaffExperiences(new ArrayList<SmStaffExperience>());
		shr.setSmRelatedCompanies(new ArrayList<SmRelatedCompany>());
		shr.setSmAssociationBodies(new ArrayList<SmAssociationBody>());
		shr.setSmPersonnelAddresses(new ArrayList<SmPersonnelAddress>());
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

	public static List<Object[]> getActiveApplList(SmSupplier smSupplier) {
		List<Object[]> resultList = new ArrayList<Object[]>();

		MofRegistrationReqDTO reqDTO = new MofRegistrationReqDTO();
		reqDTO.setSupplierId(smSupplier.getSupplierId());
		reqDTO.setLangCode(new FacesScopes().getUserLocale().getLanguage());
		resultList = SmServiceLookup.getMofRegistrationSO()
				.retrieveActiveApplUsingNativeQuery(reqDTO).getResultList();

		return resultList;
	}

	public static void clonePoApplSectionReview(
			SmApplicationManagedBean managedBean) {
		SmApplSectionReview apprApplSecRev = new SmApplSectionReview();
		for (SmApplSectionReview applSecRev : managedBean.getSmAppl()
				.getSmApplSectionReviews()) {
			//#35086 - start
			String[] exclude = new String[] {"applSectionReviewId", "associateRemark",
					"bankRemark", "basicRemark", "branchRemark", "capitalRemark",
					"categoryRemark", "certRemark", "equityCompRemark",
					"experienceRemark", "factoryRemark", "assocBodyRemark",
					"machineryRemark", "supplierSystemRemark", "manufacturingRemark",
					"officeFacilityRemark", "personnelRemark", "licenseRemarks",
					"shareholderRemark", "sitevisitRemark", "siteVisitVerifiedRemark",
					"staffRemark", "trnsNetworkRemark"};
			EntityHelper.copyEntityProperties(applSecRev, apprApplSecRev, exclude);
			//#35086 - end
			apprApplSecRev.setSmAppl(managedBean.getSmAppl());
			apprApplSecRev
					.setRoleType(SupplierManagementConst.SECTION_REV_ROLE_APP);
			apprApplSecRev.setCheckCount(0);
			apprApplSecRev.setRecommendation(null);
			apprApplSecRev.setRemarkToApprover(null);
			apprApplSecRev.setPmRegStatus(null);
			//#31043 start
			apprApplSecRev.setSitevisitVerified(true);
			//#31043 end
			managedBean.setApprvrSecReview(apprApplSecRev);
		}
		managedBean.getSmAppl().getSmApplSectionReviews()
				.add(managedBean.getApprvrSecReview());
	}

	public static void updateSmSupplierInfo(
			SmApplicationManagedBean managedBean, ReviewApplicationReqDTO reqDTO) {

		MofRegistrationReqDTO mofRegistrationReqDTO = new MofRegistrationReqDTO();
		SmSupplier supplier = managedBean.getSmSupplier();
		supplier.setEstablishDate(reqDTO.getEstablishDate());

		mofRegistrationReqDTO.setSmSupplier(supplier);

		IMofRegistrationSO reqSO = (IMofRegistrationSO) BusinessDelegate
				.getInstance().getServiceObject(
						ModuleCodeConst.SUPPLIER_MANAGEMENT,
						IMofRegistrationSO.class);
		reqSO.updateSupplierProfile(mofRegistrationReqDTO);
	}

	// Enhancement #20467
	/**
	 * Check whether the rejected application was expired compare to no. of days
	 * allowed to resubmit after rejected
	 * 
	 * @param SmAppl
	 *            rejAppl * @return Boolean
	 */
	public static Boolean checkRejectedApplExpired(SmAppl rejAppl) {
		Boolean isExpired = false;

		if (rejAppl.getApplId() != null) {
			if (!isRejectedApplValidForResubmission(rejAppl)) {
				isExpired = true;
			}
		}
		return isExpired;
	}

	/**
	 * Get latest rejected application by supplier ID and application type
	 * 
	 * @param Long
	 *            supplierId, String applType
	 * 
	 * @return SmAppl
	 */
	public static SmAppl getLatestRejectedApplIdBySuppIdAndApplType(
			Long supplierId, String applType) {
		SmAppl smAppl = null;
		MofRegistrationReqDTO reqDTO = new MofRegistrationReqDTO();
		reqDTO.setSupplierId(supplierId);
		reqDTO.setAppType(applType);
		Long applId = SmServiceLookup.getMofRegistrationSO()
				.retrieveLastRejectedApplIdByApplType(reqDTO).getApplId();
		if (applId != null) {
			reqDTO = new MofRegistrationReqDTO();
			reqDTO.setApplId(applId);
			smAppl = SmServiceLookup.getMofRegistrationSO()
					.retrieveSmApplicationDetailsById(reqDTO).getSmAppl();
		}
		return smAppl;
	}
	
	public String calculatePoKpiByCompanyState(int docId, int statusId) {
		String retVal = StringUtils.EMPTY;
		MofRegistrationReqDTO reqApplId = new MofRegistrationReqDTO();
		reqApplId.setApplId(docId);
		// reqApplId.setNotNeedInitialize(true);
		MofRegistrationResDTO resApplId = SmServiceLookup
				.getMofRegistrationSO().retrieveSmApplicationDetailsById(
						reqApplId);
		SmAppl smAppl = resApplId.getSmAppl();

		IPmKpiSO pmKpiSO = BusinessDelegate.getInstance().getServiceObject(
				ModuleCodeConst.PROFILE_MANAGEMENT, IPmKpiSO.class);
		KpiReqDTO reqDTO = new KpiReqDTO();
		reqDTO.setModuleCode(StringUtils
				.upperCase(ModuleCodeConst.SUPPLIER_MANAGEMENT));
		reqDTO.setKpiCode("SM018");
		reqDTO.setSupplierType(smAppl.getSupplierType());
		reqDTO.setApplTypeCategory(smAppl.getApplType()
				+ smAppl.getApplCategoryType());
		reqDTO.setZoneArea(smAppl.getSmCompanyBasic().getSmCompanyAddresses()
				.get(0).getSmAddress().getPmState().getZoneArea());
		Long stateId = smAppl.getSmCompanyBasic().getSmCompanyAddresses().get(0)
				.getSmAddress().getPmState().getStateId();
		PmKPIValue pmKPIValue = pmKpiSO.retrieveKpiValueByModuleCodeAndKpiCode(
				reqDTO).getKpiValue();

		if (pmKPIValue != null) {
			SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/YYYY HH:mm");
			int numDays = pmKPIValue.getKpiValue();
			Map.Entry<Integer, Date> startDateMap = getPoKpiStartDate(
					new Long(docId), stateId).entrySet().iterator().next();
			Date startDate = startDateMap.getValue();
			Integer pauseDays = startDateMap.getKey();
			Map.Entry<Integer, Date> entry = getPoKpiEndDateByCompanyState(
					startDate, numDays, pauseDays, stateId).entrySet()
					.iterator().next();
			Date endDate = entry.getValue();
			Integer daysLeft = entry.getKey();

			retVal = sdf.format(endDate)
					+ "<br />"
					+ "( <span class='blink_text'><font color='red'>"
					+ daysLeft 
					+ " " 
					+ (SmCommonUtil.getMessage("lbl.sm.po.days",
							null)) + " </font></span>) ";
		}

		return retVal;
	}
	
	private Map<Integer, Date> getPoKpiStartDate(Long docId, Long stateId) {
		int pauseDays = 0;
		Date startDate = new Date();
		Map<Integer, Date> mapDate = new HashMap<Integer, Date>();
		SmWorkFlowStatusReqDTO swReqDTO = new SmWorkFlowStatusReqDTO();
		swReqDTO.setStatusIds(Arrays
				.asList(new Long[] {
						my.ep.constant.sm.SupplierManagementConst.PM_STATUS_PENDING_REVIEW_APPLICATION,
						my.ep.constant.sm.SupplierManagementConst.PM_STATUS_PO_INQUIRY }));
		swReqDTO.setApplId(docId);
		List<SmWorkflowStatus> workFlowList = SmServiceLookup
				.getSmWorkFlowStatusSO()
				.retrieveWorkflowByDocIdAndMultipleStatus(swReqDTO)
				.getWorkFlowStatusLs();
		sortCreatedDateAsc(workFlowList);

		List<SmWorkflowStatus> inquiryTmpList = new ArrayList<SmWorkflowStatus>();

		if (workFlowList != null) {
			startDate = workFlowList.get(0).getCreatedDate();
			if (workFlowList.size() > 1) {
				Iterator<SmWorkflowStatus> wfIter = workFlowList.iterator();
				//SmWorkflowStatus wfStart = wfIter.next();
				int i = 0;
				while (wfIter.hasNext()) {
					i++;

					if (i != 1) {
						SmWorkflowStatus wf = wfIter.next();
						if (wf.getPmStatus()
								.getStatusId()
								.compareTo(
										my.ep.constant.sm.SupplierManagementConst.PM_STATUS_PO_INQUIRY) == 0) {
							inquiryTmpList.add(wf);
						} else if (wf
								.getPmStatus()
								.getStatusId()
								.compareTo(
										my.ep.constant.sm.SupplierManagementConst.PM_STATUS_PENDING_REVIEW_APPLICATION) == 0) {

							if (inquiryTmpList.size() == 1) {
								SmWorkflowStatus wfInquiry = inquiryTmpList
										.get(0);
								pauseDays = pauseDays
										+ SmCommonUtil
												.getInstance()
												.getDaysDiffMinusHolidayByState(
														wfInquiry
																.getCreatedDate(),
														wf.getCreatedDate(),
														stateId);
								inquiryTmpList.clear();
							}
						}
					}

				}
			}

		}
		mapDate.put(pauseDays, startDate);
		return mapDate;

	}
	
	private void sortCreatedDateAsc(List<SmWorkflowStatus> wfStatusList) {
		//Bug #25676 : SM Showstopper - No Categories Appear in Processing Officer Screen
		if(!EntityHelper.isCollectionEmpty(wfStatusList)){
			// sort created date asc
			Collections.sort(wfStatusList, new Comparator<SmWorkflowStatus>() {
				public int compare(SmWorkflowStatus o1, SmWorkflowStatus o2) {
					if (o2.getCreatedDate() == null || o1.getCreatedDate() == null)
						return 0;
					return o1.getCreatedDate().compareTo(o2.getCreatedDate());
				}
			});
		}
	}
	
	// 30747 - Start
	/**
	 * @param startDate
	 * @param numDays
	 * @param pauseDays
	 * @param stateId
	 * @return days left and date
	 */
	public Map<Integer, Date> getPoKpiEndDateByCompanyState(Date startDate, int numDays, int pauseDays, Long stateId) {
		int daysLeft;
		int totalDays = numDays + pauseDays;
		Map<Integer, Date> mapDate = new HashMap<>();
		Calendar dateCal = Calendar.getInstance();
		dateCal.setTime(startDate);

		calcEndDate(numDays, dateCal, stateId);

		if (pauseDays != 0) {
			calcEndDate(pauseDays, dateCal, stateId);
		}

		if (dateCal.getTime().before(new Date())) {
			daysLeft = 0;
		} else {
			daysLeft = SmCommonUtil.getInstance().getDaysDiffMinusHolidayByState(new Date(), dateCal.getTime(), stateId);

			Calendar endCal = Calendar.getInstance();
			endCal.setTime(dateCal.getTime());
			endCal.add(Calendar.DATE, 1);

			boolean isNextDayHoliday = SmCommonUtil.getInstance().isDateHoliday(endCal, stateId);

			if (!isNextDayHoliday) {
				mapDate.put(daysLeft, dateCal.getTime());
				return mapDate;
			}

			if (daysLeft == 0) {
				compareDateForKpi(new Date(), dateCal.getTime(), daysLeft);
			} else if (daysLeft < totalDays) {
				Calendar newEndCal = Calendar.getInstance();
				newEndCal.setTime(new Date());

				calcEndDate(daysLeft, newEndCal, stateId);

				if (newEndCal.getTime().before(dateCal.getTime())) {
					daysLeft++;
				}
			}
		}

		mapDate.put(daysLeft, dateCal.getTime());
		return mapDate;
	}

	private int compareDateForKpi(Date start, Date end, int daysLeft) {
		Calendar startCal = Calendar.getInstance();
		startCal.setTime(start);
		int result = daysLeft; // new local variable instead of mutating the
								// parameter
		while (startCal.getTime().before(end)) {
			result++;
			startCal.add(Calendar.DATE, 1);
		}
		return result;
	}

	private void calcEndDate(int numDays, Calendar dateCal, Long stateId) {
		int actualDays = 0;
		while (actualDays < numDays) { // #30747 change for loop to while
			dateCal.add(Calendar.DATE, 1);
			if (!SmCommonUtil.getInstance().isDateHoliday(dateCal, stateId)) {
				actualDays++;
			}
		}
	}
	// 30747 - End
	
	public void calcCapitalDistribution(SmPersonnel per,SmCompanyBasic basic){
		if (per.getShareAmt() == null) {
			per.setShareAmt(new BigDecimal(0));
		}
		basic.setBumiEquity(basic.getBumiEquity()==null?new BigDecimal(0):basic.getBumiEquity());
		basic.setNonBumiEquity(basic.getNonBumiEquity()==null?new BigDecimal(0):basic.getNonBumiEquity());
		basic.setForeignerEquity(basic.getForeignerEquity()==null?new BigDecimal(0):basic.getForeignerEquity());
		basic.setGovtUnitrustEquity(basic.getGovtUnitrustEquity()==null?new BigDecimal(0):basic.getGovtUnitrustEquity());
		basic.setGovtEquity(basic.getGovtEquity()==null?new BigDecimal(0):basic.getGovtEquity());
		basic.setForeignCompanyEquity(basic.getForeignCompanyEquity()==null?new BigDecimal(0):basic.getForeignCompanyEquity());
		basic.setLocalCompanyEquity(basic.getLocalCompanyEquity()==null?new BigDecimal(0):basic.getLocalCompanyEquity());

		if(per.isBumi() != null && per.isBumi()){
			basic.setBumiEquity(basic.getBumiEquity().add(per.getShareAmt()));
		}else if(per.isBumi() == null || !per.isBumi()){
			basic.setNonBumiEquity(basic.getNonBumiEquity().add(per.getShareAmt()));
		}else if(per.isAttachHq()){
			
		}
		
	}
	
	public static void removeEquityCompanyFromList(
			List<SmEquityCompany> equityCompanyList) {
		if (equityCompanyList != null) {
			Iterator<SmEquityCompany> iterator = equityCompanyList.iterator();
			while (iterator.hasNext()) {
				SmEquityCompany equityCompany = iterator.next();
				if (equityCompany.isSsmData()) {
					iterator.remove();
				}
			}
		}
	}
	
	//26238 - start
	/**
	 * Retrieve sm disciplinary action
	 *
	 * @param supplier
	 * @return SmDisciplinaryAction
	 */
	public static List<SmDisciplinaryAction> retrieveSmDisciplinaryActionBySupplier(SmSupplier supplier) {
		SupplierDataMaintenanceReqDTO reqDTO = new SupplierDataMaintenanceReqDTO();
		reqDTO.setSupplier(supplier);
		SupplierDataMaintenanceResDTO resDTO = SmServiceLookup.getSupplierDataMaintenanceSO().
				retrieveDisciplinaryActionBySupplier(reqDTO);

		return resDTO.getSmDisciplinaryActions();
	} 
	
	/**
	 * 
	 * @param supplier
	 * @return
	 */
	public static List<SmDisciplinaryAction> retrieveActiveSmDisciplinaryActionBySupplier(SmSupplier supplier) {
		SupplierDataMaintenanceReqDTO reqDTO = new SupplierDataMaintenanceReqDTO();
		reqDTO.setSupplier(supplier);
		
		SupplierDataMaintenanceResDTO resDTO = SmServiceLookup.getSupplierDataMaintenanceSO().
				retrieveActiveDisciplinaryActionBySupplier(reqDTO);

		return resDTO.getSmDisciplinaryActions();
	} 
	
	//30865 - Start
	/**
	 * 
	 * @param disciplinaryId
	 * @return
	 */
	public static SmDisciplinaryAction retrieveActiveSmDisciplinaryActionByDisciplinaryId(Long disciplinaryId) {
		if (disciplinaryId == null) {
			return null;
		}
		
		return SmServiceLookup.getSupplierDataMaintenanceSO()
				.retrieveActiveDisciplinaryActionByDisciplinaryId(disciplinaryId);
	}  
	//30865 - End
	
	
	/**
	 * Get supplier category by supplierId and isCancelCategory
	 * 
	 * @return List<SmSupplierCategory>
	 */
	public static List<SmSupplierCategory> retrieveSmSupplierCategory() {
		ISupplierCategorySO supplierCategorySO = (ISupplierCategorySO) BusinessDelegate.getInstance()
				.getServiceObject(ModuleCodeConst.SUPPLIER_MANAGEMENT, ISupplierCategorySO.class);
		
		SupplierCategoryReqDTO reqDTO = new SupplierCategoryReqDTO();
		SupplierCategoryResDTO resDTO = new SupplierCategoryResDTO();
		Long supplierId = getCurrentSupplier().getSupplierId();
		
//		reqDTO.setApplId(getCurrentSupplier().getSmAppl().getApplId());
		reqDTO.setSupplierId(supplierId);
		
		resDTO = supplierCategorySO.retrieveCategoryBySupplierId(reqDTO);
		return resDTO.getSupplierCategoryList();
	}
	
	@SuppressWarnings("unchecked")
	public static List<Object[]> retrieveCancelCategory() {
		ISupplierCategorySO supplierCategorySO = (ISupplierCategorySO) BusinessDelegate.getInstance()
				.getServiceObject(ModuleCodeConst.SUPPLIER_MANAGEMENT, ISupplierCategorySO.class);
		
		SupplierCategoryReqDTO reqDTO = new SupplierCategoryReqDTO();
		SupplierCategoryResDTO resDTO = new SupplierCategoryResDTO();
		Long supplierId = getCurrentSupplier().getSupplierId();
		
		reqDTO.setSupplierId(supplierId);
		
		resDTO = supplierCategorySO.retrieveCancelCategoryDetails(reqDTO);
		return resDTO.getResultList();
	}
	
	/**
	 * 
	 * @param supplier
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static List<Object[]> retrieveCancelCategoryBySupplier(SmSupplier supplier) {
		SupplierCategoryReqDTO reqDTO = new SupplierCategoryReqDTO();
		Long supplierId = supplier.getSupplierId();
		
		reqDTO.setSupplierId(supplierId);
		
		SupplierCategoryResDTO resDTO = SmServiceLookup.getSupplierCategorySO().
				retrieveCancelCategoryDetails(reqDTO);
		return resDTO.getResultList();
	}
	//26238 - end
	
	//27617 : Disciplinary Action - 26238 - start
	/**
	 * Retrieve Cancel Category
	 * @param disciplinaryActionId
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static List<Object[]> retrieveCancelCategoryByDisciplinaryActionId(Long disciplinaryActionId) {
		SupplierCategoryReqDTO reqDTO = new SupplierCategoryReqDTO();
		
		reqDTO.setDisciplinaryActionId(disciplinaryActionId);
		
		SupplierCategoryResDTO resDTO = SmServiceLookup.getSupplierCategorySO().
				retrieveCancelCategoryByDisciplinaryActionId(reqDTO);
		return resDTO.getResultList();
	} 
	//27617 : Disciplinary Action - 26238 - end
		
	//28281 - start
	public static List<Long> retrieveBumiCodeList(){
		ISupplierCategorySO supplierCategorySO = (ISupplierCategorySO) BusinessDelegate.getInstance()
				.getServiceObject(ModuleCodeConst.SUPPLIER_MANAGEMENT, ISupplierCategorySO.class);
		return supplierCategorySO.retrieveBumiCodeList();
	}
	//28281 - end	
	
	//28198: Disciplinary Action - start - 26238
	/**
	 * Retrieve Active Disciplinary Action Remarks
	 * @param da
	 * @return
	 */
	public static List<SmDisciplinaryActionRemarks> retrieveActiveSmDisciplinaryActionRemarksByDA(SmDisciplinaryAction da){
		SupplierDataMaintenanceReqDTO reqDTO = new SupplierDataMaintenanceReqDTO();
		reqDTO.setSmDisciplinaryAction(da);
		SupplierDataMaintenanceResDTO resDTO = SmServiceLookup.getSupplierDataMaintenanceSO().
				retrieveDisciplinaryActionRemarksByDAId(reqDTO);
		return resDTO.getSmDisciplinaryActionsRemarks();
	}
			
	//28198: Disciplinary Action - end - 26238
			
	//24964 - start 
	public static void rejectSmMofCertForSupplier(SmSupplier supplier){
		VirtualCertReqDTO reqDTO = new VirtualCertReqDTO();
		reqDTO.setSupplierId(supplier.getSupplierId());
				
		VirtualCertResDTO resDTO = SmServiceLookup.getVirtualCertSO().retrieveMOFCertBySupplierId(reqDTO);
		
		if(resDTO != null && resDTO.getSmMofCerts() != null){
			for(SmMofCert cert : resDTO.getSmMofCerts()){
				if(cert.isBumiCert() 
						&& cert.getRecordStatusFlg().equals(DbValueConst.RECORD_STATUS_ACTIVE)){
					VirtualCertReqDTO rejectedMofCert = new VirtualCertReqDTO();
					MofRegistrationReqDTO mofRegistrationReqDTO = new MofRegistrationReqDTO();
					
					//update sm_supplier: is_bumi to 0
					supplier.setBumi(false);
					mofRegistrationReqDTO.setSmSupplier(supplier);
					SmServiceLookup.getMofRegistrationSO().updateSupplierProfile(mofRegistrationReqDTO);					
					
					//update record_status = 4
					cert.setRecordStatusFlg(DbValueConst.RECORD_STATUS_REJECTED);					
					rejectedMofCert.setMofCert(cert);
					SmServiceLookup.getVirtualCertSO().updateMOFCert(rejectedMofCert);
				}
			}
		}
	}
	//24964 - end
	
	//#29315 - start
	public static void populateMandatoryEpfNo(SmApplicationManagedBean bean){
		if (bean.getSelectedShareHolder().isProf()
				|| bean.getSelectedShareHolder().isSemiProf()
				|| bean.getSelectedShareHolder().isClerical()) {
			bean.setFlagMandatoryEpfNo(true);
			if (!EntityHelper.isCollectionEmpty(bean.getSmAppl().getSmPersonnels())) {
				for (SmPersonnel sp : bean.getSmAppl().getSmPersonnels()) {
					if ( (!sp.getIdentificationNo().equalsIgnoreCase(bean.getSelectedShareHolder().getIdentificationNo()) 
							&& StringUtils.isNotBlank(sp.getEpfNo())) 
							&& ((bean.getSelectedShareHolder().isProf() && sp.isProf()) 
									|| (bean.getSelectedShareHolder().isSemiProf() && sp.isSemiProf())
									|| (bean.getSelectedShareHolder().isClerical() && sp.isClerical()))) {
						bean.setFlagMandatoryEpfNo(false);
						break;
					}
				}
			}
		} else {
			bean.setFlagMandatoryEpfNo(false);
		}
	}
	//#29315 - end

	//13320-start
	public void populateDivisionsByState(SmAddress addr, String panelName, SmApplicationManagedBean bean){		

		try {
			Long stateId = addr.getStateId();
			if (stateId != null) {
				addr.setPmState(SmCommonUtil.getInstance()
						.getStateById(stateId));
			}
			DivisionReqDTO reqDTO = new DivisionReqDTO();
			reqDTO.setStateId(stateId);
			IDivisionSO reqSO = (IDivisionSO) BusinessDelegate.getInstance()
					.getServiceObject("pm", IDivisionSO.class);
			DivisionResDTO resDTO = reqSO.retriveDivisionListByState(reqDTO);
			addr.setDivisionId(null);
			List<PmDivision> divLs = resDTO.getDivisionList();
			List<SelectItem> divItems = SmCommonUtil.getDivisionList(stateId);
			addr.getPmState().setAdoptDivision(true);
			switch (panelName) {
			case SupplierManagementConst.SM_BASIC_INFO_PANEL:
				bean.setBasicCompanyDivisions(divItems);
				bean.setBasicCompanyDistricts(new ArrayList<SelectItem>());
				bean.setBasicCompanyCities(null);
				break;
			case SupplierManagementConst.SM_OFFICE_FACILITY_INFO_PANEL:
				bean.setBasicOffFacDivisions(divItems);
				bean.setBasicOffFacDistricts(new ArrayList<SelectItem>());
				bean.setBasicOffFacCities(null);
				break;

			case SupplierManagementConst.SM_BRANCH_PANEL:
				bean.setBranchCompanyDivisions(divItems);
				bean.setBranchCompanyDistricts(new ArrayList<SelectItem>());
				bean.setBranchCompanyCities(null);
				break;
			case SupplierManagementConst.SM_ASSOC_COMPANY_PANEL:
				bean.setAssocCompanyDivisions(divItems);
				bean.setAssocCompanyDistricts(new ArrayList<SelectItem>());
				bean.setAssocCompanyCities(null);
				break;
			case SupplierManagementConst.SM_EQUI_COMPANY_PANEL:
				bean.setEquiCompanyDivisions(divItems);
				bean.setEquiCompanyDistricts(new ArrayList<SelectItem>());
				bean.setEquiCompanyCities(null);
				break;

			case SupplierManagementConst.SM_FACTORY_FACILITY_PANEL:
				bean.setFactoryFacilityDivisions(divItems);
				bean.setFactoryFacilityDistricts(null);
				bean.setFactoryFacilityCities(new ArrayList<SelectItem>());
				break;
			case SupplierManagementConst.SM_DOCKYARD_FACILITY_PANEL:
				bean.setDockFacilityDivisions(divItems);
				bean.setDockFacilityDistricts(new ArrayList<SelectItem>());
				bean.setDockFacilityCities(null);
				break;
			case SupplierManagementConst.SM_WORKSHOP_FACILITY_PANEL:
				bean.setWorkShopFacilityDivisions(divItems);
				bean.setWorkShopFacilityDistricts(new ArrayList<SelectItem>());
				bean.setWorkShopFacilityCities(null);
				break;
			case SupplierManagementConst.SM_STORE_FACILITY_PANEL:
				bean.setStoreFacilityDivisions(divItems);
				bean.setStoreFacilityDistricts(new ArrayList<SelectItem>());
				bean.setStoreFacilityCities(null);
				break;
			case SupplierManagementConst.SM_SHRHLDR_PERM_ADDR_PANEL:
				bean.setShrHldrPermDivisions(divItems);
				bean.setShrHldrPermDistricts(new ArrayList<SelectItem>());
				bean.setShrHldrPermCities(null);
				break;
			case SupplierManagementConst.SM_SHRHLDR_RESD_ADDR_PANEL:
				bean.setShrHldrResdDivisions(divItems);
				bean.setShrHldrResdDistricts(new ArrayList<SelectItem>());
				bean.setShrHldrResdCities(null);
				break;
			case SupplierManagementConst.SM_SHRHLDR_OFF_ADDR_PANEL:
				bean.setShrHldrOffDivisions(divItems);
				bean.setShrHldrOffDistricts(new ArrayList<SelectItem>());
				bean.setShrHldrOffCities(null);
				break;
			case SupplierManagementConst.SM_SHRHLDR_MAIL_ADDR_PANEL:
				bean.setShrHldrMailDivisions(divItems);
				bean.setShrHldrMailDistricts(new ArrayList<SelectItem>());
				bean.setShrHldrMailCities(null);
				break;
			case SupplierManagementConst.SM_PERSNL_PERM_ADDR_PANEL:
				bean.setPersnlPermDivisions(divLs);
				bean.setPersnlPermDistricts(new ArrayList<PmDistrict>());
				bean.setPersnlPermCities(null);
				break;
			case SupplierManagementConst.SM_PERSNL_RESD_ADDR_PANEL:
				bean.setPersnlResdDivisions(divLs);
				bean.setPersnlResdDistricts(new ArrayList<PmDistrict>());
				bean.setPersnlResdCities(null);
				break;
			case SupplierManagementConst.SM_PERSNL_OFF_ADDR_PANEL:
				bean.setPersnlOffDivisions(divLs);
				bean.setPersnlOffDistricts(new ArrayList<PmDistrict>());
				bean.setPersnlOffCities(null);
				break;
			case SupplierManagementConst.SM_PERSNL_MAIL_ADDR_PANEL:
				bean.setPersnlMailDivisions(divLs);
				bean.setPersnlMailDistricts(new ArrayList<PmDistrict>());
				bean.setPersnlMailCities(null);
				break;
			case SupplierManagementConst.SM_BANK_INFO_PANEL:
				bean.setBankDivisions(divItems);
				bean.setBankDistricts(new ArrayList<SelectItem>());
				bean.setBankCities(null);
				break;
			default:
				//log.info("**** This is an unknown panel ***** ");
				break;
			}
			addr.setDivisionId(null);
			addr.setDistrictId(null);
			addr.setCityId(null);
		} catch (Exception e) {
			e.printStackTrace();
		}	
	}
	//13320-end	
	
	//13320-start
	public void populateDistrictsByUsingDivision(SmAddress addr, String panelName,
			SmApplicationManagedBean bean) {
		try {
			Long divisionId = addr.getDivisionId();
			//#13320 - SmCommonUtil change method
			addr.setPmDivision(SmCommonUtil.getInstance().getDivisionByUsingId(
					divisionId));
			DistrictReqDTO reqDTO = new DistrictReqDTO();
			reqDTO.setDistrictId(divisionId);
			IDistrictSO reqSO = (IDistrictSO) BusinessDelegate.getInstance()
					.getServiceObject("pm", IDistrictSO.class);
			DistrictResDTO resDTO = reqSO.retriveDistrictListByDivision(reqDTO);
			List<PmDistrict> diviLs = resDTO.getDistrictList();
			List<SelectItem> divisionLs = SmCommonUtil.getDistrictListByID(divisionId);
			switch (panelName) {
			case SupplierManagementConst.SM_BASIC_INFO_PANEL:
				bean.setBasicCompanyDistricts(divisionLs);
				bean.setBasicCompanyCities(new ArrayList<SelectItem>());
				break;
			case SupplierManagementConst.SM_OFFICE_FACILITY_INFO_PANEL:
				bean.setBasicOffFacDistricts(divisionLs);
				bean.setBasicOffFacCities(new ArrayList<SelectItem>());
				break;

			case SupplierManagementConst.SM_BRANCH_PANEL:
				bean.setBranchCompanyDistricts(divisionLs);
				bean.setBranchCompanyCities(new ArrayList<SelectItem>());
				break;
			case SupplierManagementConst.SM_ASSOC_COMPANY_PANEL:
				bean.setAssocCompanyDistricts(divisionLs);
				bean.setAssocCompanyCities(new ArrayList<SelectItem>());
				break;
			case SupplierManagementConst.SM_EQUI_COMPANY_PANEL:
				bean.setEquiCompanyDistricts(divisionLs);
				bean.setEquiCompanyCities(new ArrayList<SelectItem>());
				break;

			case SupplierManagementConst.SM_FACTORY_FACILITY_PANEL:
				bean.setFactoryFacilityDistricts(divisionLs);
				bean.setFactoryFacilityCities(new ArrayList<SelectItem>());
				break;
			case SupplierManagementConst.SM_DOCKYARD_FACILITY_PANEL:
				bean.setDockFacilityDistricts(divisionLs);
				bean.setDockFacilityCities(new ArrayList<SelectItem>());
				break;
			case SupplierManagementConst.SM_WORKSHOP_FACILITY_PANEL:
				bean.setWorkShopFacilityDistricts(divisionLs);
				bean.setWorkShopFacilityCities(new ArrayList<SelectItem>());
				break;
			case SupplierManagementConst.SM_STORE_FACILITY_PANEL:
				bean.setStoreFacilityDistricts(divisionLs);
				bean.setStoreFacilityCities(new ArrayList<SelectItem>());
				break;
			case SupplierManagementConst.SM_SHRHLDR_PERM_ADDR_PANEL:
				bean.setShrHldrPermDistricts(divisionLs);
				bean.setShrHldrPermCities(new ArrayList<SelectItem>());
				break;
			case SupplierManagementConst.SM_SHRHLDR_RESD_ADDR_PANEL:
				bean.setShrHldrResdDistricts(divisionLs);
				bean.setShrHldrResdCities(new ArrayList<SelectItem>());
				break;
			case SupplierManagementConst.SM_SHRHLDR_OFF_ADDR_PANEL:
				bean.setShrHldrOffDistricts(divisionLs);
				bean.setShrHldrOffCities(new ArrayList<SelectItem>());
				break;
			case SupplierManagementConst.SM_SHRHLDR_MAIL_ADDR_PANEL:
				bean.setShrHldrMailDistricts(divisionLs);
				bean.setShrHldrMailCities(new ArrayList<SelectItem>());
				break;
			case SupplierManagementConst.SM_PERSNL_PERM_ADDR_PANEL:
				bean.setPersnlPermDistricts(diviLs);
				bean.setPersnlPermCities(new ArrayList<PmCity>());
				break;
			case SupplierManagementConst.SM_PERSNL_RESD_ADDR_PANEL:
				bean.setPersnlResdDistricts(diviLs);
				bean.setPersnlResdCities(new ArrayList<PmCity>());
				break;
			case SupplierManagementConst.SM_PERSNL_OFF_ADDR_PANEL:
				bean.setPersnlOffDistricts(diviLs);
				bean.setPersnlOffCities(new ArrayList<PmCity>());
				break;
			case SupplierManagementConst.SM_PERSNL_MAIL_ADDR_PANEL:
				bean.setPersnlMailDistricts(diviLs);
				bean.setPersnlMailCities(new ArrayList<PmCity>());
				break;
			case SupplierManagementConst.SM_BANK_INFO_PANEL:
				bean.setBankDistricts(divisionLs);
				bean.setBankCities(new ArrayList<SelectItem>());
				break;
			default:
				//log.info("**** This is an unknown panel ***** ");
				break;
			}
			addr.setDistrictId(null);
			addr.setCityId(null);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	//13320-end
		
	//STATR 25474
	public static SmAppl getLatesRejectedApplicationBySuppIdAndAppTypeNoExpired(Long supplierId, String applType) {
		SmAppl smAppl = null;
		StringBuilder sb = new StringBuilder();
		sb.append("(")
				.append(SupplierManagementConst.PM_STATUS_REJECTED_BY_PROCESSING_OFFICER)
				.append(",")
				.append(SupplierManagementConst.PM_STATUS_REJECTED_BY_APPROVER)
				.append(",")
				.append(SupplierManagementConst.PM_STATUS_REJECTED_BY_SYSTEM)
				//#32627 START
				.append(",")
				.append(SupplierManagementConst.PM_STATUS_REJECTED_BY_SYSTEM_SDO_INQUIRY)
				.append(",")
				.append(SupplierManagementConst.PM_STATUS_REJECTED_BY_SYSTEM_PO_INQUIRY)
				.append(",")
				.append(SupplierManagementConst.PM_STATUS_AUTOREJECT_UNSUBMIT_OFFLINE_DOCUMENT)
				//#32627 END
				.append(")");
		MofRegistrationReqDTO reqDTO = new MofRegistrationReqDTO();
		reqDTO.setNotUsedConditionExpired(true);
		reqDTO.setCurrent(true);
		reqDTO.setSupplierId(supplierId);
		reqDTO.setWorkflowStatus(sb.toString());
		reqDTO.setAppType(applType);
		Long applId = SmServiceLookup.getMofRegistrationSO()
				.retriveApplIdForLastRejectedApplicationByApplType(reqDTO)
				.getApplId();
		if (applId != null) {
			reqDTO = new MofRegistrationReqDTO();
			reqDTO.setApplId(applId);
			smAppl = SmServiceLookup.getMofRegistrationSO()
					.retrieveSmApplicationDetailsById(reqDTO).getSmAppl();
		}
		return smAppl;
	}
	//END 25474
	
	//#31627 - START
	public static void ajaxRemoveSelectedSSMBranches(List<SmSupplierBranch> branchesList) {
		if (branchesList != null) {
			Iterator<SmSupplierBranch> iterator = branchesList.iterator();
			while (iterator.hasNext()) {
				SmSupplierBranch smSupplierBranch = iterator.next();
				if (smSupplierBranch.isSsmData()) {
					iterator.remove();
				}
			}
		}
	}
	//31627 - END
	
	//#33303 - START
	/**
	 * Sort SmSupplierCategory by category type and category level code
	 * 
	 * @param List DisciplinaryActionCategoryVO
	 */
	public static void sortSupplierCategoryDA(List<DisciplinaryActionCategoryVO> apprvList) {
		// sort by code
		Collections.sort(apprvList, new Comparator<DisciplinaryActionCategoryVO>() {
			@Override
			public int compare(final DisciplinaryActionCategoryVO s1,
					final DisciplinaryActionCategoryVO s2) {
				return (s1.getCategoryL1Code()
						+ s1.getCategoryL2Code() + s1
						.getCategoryL3Code()).compareTo(s2
						.getCategoryL1Code()
						+ s2.getCategoryL2Code()
						+ s2.getCategoryL3Code());
			}
		});
		
		
	}
	//#33303 - END
	
	//#32008 - after approval bug
	private static boolean isActiveCategoryAfterApproval(SmSupplierCategory cat, Long applId){
		return cat.getRecordStatusFlg().intValue() == SupplierManagementConst.APPROVED_RECORD_STATUS
				&& applId.equals(cat.getApplRefId());
	}
	
	//#34483 START
	public static boolean isApplPORejectedAfterApprovelSV(SmAppl appl){
		boolean isRejected = false;
		boolean isHasBasic = false;
		if(appl.getPmStatus().getStatusId() == 20112){
			for(SmWorkflowStatus smWorkflowStatus : appl.getSmWorkflowStatuses()){
				if(smWorkflowStatus.getPmStatus().getStatusId() == 20116 || smWorkflowStatus.getPmStatus().getStatusId() == 20117){
					isRejected = true;
					break;
				}
			}
		}
		
		for(SmAppl previousSmAppl : appl.getSmSupplier().getSmAppls()){
			if(previousSmAppl.getSupplierType().equalsIgnoreCase(SupplierManagementConst.SUPP_TYPE_BASIC)){
				isHasBasic = true;
				break;
			}
		}
		
		if(appl.getApplType().equalsIgnoreCase(SupplierManagementConst.ACC_MOF) && isRejected && !isHasBasic){
			return false;
		}else if((appl.getApplType().equalsIgnoreCase(SupplierManagementConst.APPL_TYPE_CATEGORY)
				|| appl.getApplType().equalsIgnoreCase(SupplierManagementConst.APPL_TYPE_BUMI)) && isRejected){ 
			return false;
			
		}
		
		return true;
	}
	//#34483 END
	
	//#31043 - START
	public static void checkCategoryRemark(SmApplicationManagedBean bean){
		if (bean.getSmAppl().getApplType().equals(ParameterConst.NEW_SM_APPLN_FOR_RENEW)) {
			if (SmCommonUtil.isCollectionEmpty(bean.getActiveCategoryList()) && SmCommonUtil.isCollectionEmpty(bean.getApprovedCategoryList())) {
				assignActiveAndApprovedCategoryList(bean);
			}
		} else {
			if (SmCommonUtil.isCollectionEmpty(bean.getActiveCategoryList())) {
				assignActiveAndApprovedCategoryList(bean);
			}
		}
		log.info("Appl type: "+ bean.getSmAppl().getApplType());
		if (!SmCommonUtil.isCollectionEmpty(bean.getActiveCategoryList())) {
			for (SmSupplierCategory cat : bean.getActiveCategoryList()) {
				if (bean.getSmAppl().getApplType().equals(ParameterConst.NEW_SM_APPLN_FOR_MOF) ||
						bean.getSmAppl().getApplType().equals(ParameterConst.NEW_SM_APPLN_FOR_APPLCATCODES) ||
						bean.getSmAppl().getApplType().equals(ParameterConst.NEW_SM_APPLN_FOR_RENEW)) {
					if(bean.isPoUser()){ //#36576
						if(cat.getPoDecFlg() != null && cat.getPoDecFlg() == 0 
								&& (cat.getPoRemarks() == null || cat.getPoRemarks().isEmpty())){
							throw new EpException(SmCommonUtil.getMessage(
									"lbl.sm.required.msg",
									new String[] { SmCommonUtil.getMessage(
											"lbl.sm.catCodes.rejRemarks",
											null)
											+ " "
											+ cat.getPmCategoryL1().getCategoryL1Code()
											+ cat.getPmCategoryL2().getCategoryL2Code()
											+ cat.getPmCategoryL3().getCategoryL3Code() }));
						}
					}else if(bean.isApproverUser()){ //#36576
						if(cat.getApprvrDecFlg() != null && cat.getApprvrDecFlg() == 0 
								&& (cat.getApproverRemarks() == null || cat.getApproverRemarks().isEmpty())){
							throw new EpException(SmCommonUtil.getMessage(
									"lbl.sm.required.msg",
									new String[] { SmCommonUtil.getMessage(
										"lbl.sm.catCodes.rejRemarks",
											null)
												+ " "
												+ cat.getPmCategoryL1().getCategoryL1Code()
												+ cat.getPmCategoryL2().getCategoryL2Code()
												+ cat.getPmCategoryL3().getCategoryL3Code() }));
						}
					}
				}
			}
		}
			
		if (!SmCommonUtil.isCollectionEmpty(bean
				.getApprovedCategoryList())) {
			for (SmSupplierCategory cat : bean.getApprovedCategoryList()) {
				if (bean.getSmAppl().getApplType().equals(ParameterConst.NEW_SM_APPLN_FOR_RENEW)) {
					if(bean.isPoUser()){ //#36576
						if(cat.getPoDecFlg() != null && cat.getPoDecFlg() == 0 
								&& (cat.getPoRemarks() == null || cat.getPoRemarks().isEmpty())){
							throw new EpException(SmCommonUtil.getMessage(
									"lbl.sm.required.msg",
									new String[] { SmCommonUtil.getMessage(
											"lbl.sm.catCodes.rejRemarks",
											null)
											+ " "
											+ cat.getPmCategoryL1().getCategoryL1Code()
											+ cat.getPmCategoryL2().getCategoryL2Code()
											+ cat.getPmCategoryL3().getCategoryL3Code() }));
						}
					}else if(bean.isApproverUser()){ //#36576
						if(cat.getApprvrDecFlg() != null && cat.getApprvrDecFlg() == 0 
								&& (cat.getApproverRemarks() == null || cat.getApproverRemarks().isEmpty())){
							throw new EpException(SmCommonUtil.getMessage(
									"lbl.sm.required.msg",
									new String[] { SmCommonUtil.getMessage(
											"lbl.sm.catCodes.rejRemarks",
											null)
											+ " "
											+ cat.getPmCategoryL1().getCategoryL1Code()
											+ cat.getPmCategoryL2().getCategoryL2Code()
											+ cat.getPmCategoryL3().getCategoryL3Code() }));
						}
					}
				}
			}
		}
	}
	
	//28778 - Site Visit Grouping Enhancement -28775 - start
	/**
	 * @param appl
	 * @return true if appl has special category codes
	 */
	public static boolean hasSpecialCategory(SmAppl appl){
		if(appl == null || appl.getSmSupplierCategories() == null){
			return false;
		}
		List<Integer> validStatus = Arrays.asList(DbValueConst.RECORD_STATUS_PENDING_RE_APPROVAL,
				DbValueConst.RECORD_STATUS_ACTIVE, DbValueConst.RECORD_STATUS_PENDING_ACTIVATION);
		for(SmSupplierCategory suppCat : appl.getSmSupplierCategories()){
			if (suppCat.isSpecialCategory() && validStatus.contains(suppCat.getRecordStatusFlg())){
				return true;
		    }
		}
		return false;
	}
	//28778 - Site Visit Grouping Enhancement -28775 - end
	//29380 - Site Visit Grouping Enhancement -28775 -sStart
	/**
	 * @param suppCats
	 * @param catL3
	 * @return other description for category code manufacturing/dockyard
	 */
	public static String getCategoryCodeOtherDesc(List<SmSupplierCategory> suppCats, PmCategoryL3 catL3){
		if(suppCats == null || suppCats.isEmpty() || catL3 == null){
			return null;
		}
		String param = catL3.getCategoryTypeCode().getParameterCode();
		if(!param.equals(SupplierManagementConst.CAT_TYPE_DOCKYARD) 
				&& !param.equals(SupplierManagementConst.CAT_TYPE_MANUFACTURING)){
			return null;
		}
		SmSupplierCategory sCat = (SmSupplierCategory) EntityHelper.getEntityBySinglePropertyFromList(
				catL3.getCategoryL3Id(), suppCats, "pmCategoryL3");
		String otherLbl = SmCommonUtil.getMessage("lbl.sm.catCodes.othDesc", null)+": ";
		return sCat != null ? otherLbl + sCat.getOtherDesc() : null;
	}
	
	private static boolean isValidCategory(SmSupplierCategory suppCat, SmAppl appl){
		if (suppCat == null || appl == null){
			return false;
		}
		int status = suppCat.getRecordStatusFlg().intValue();
		boolean isValid = status == DbValueConst.RECORD_STATUS_PENDING_RE_APPROVAL
				|| status == DbValueConst.RECORD_STATUS_PENDING_ACTIVATION
				|| isActiveCategoryAfterApproval(suppCat, appl.getApplId());
		if (appl.getApplType().equals(ParameterConst.NEW_SM_APPLN_FOR_RENEW)){
			isValid |= status == DbValueConst.RECORD_STATUS_ACTIVE;
		}
		return isValid;
	}
	//29380, 26671 - Site Visit Grouping Enhancement -28775 - end
	/**
	 * Check if the Approver or PO is allowed allowed to recommend to site visit application.
	 * 
	 * @param managedBean
	 *            - SmApplicationManagedBean object
	 */
	public static void checkAllowedToRecommendSiteVisit(SmApplicationManagedBean managedBean) {
		boolean allowSiteVisit = false;
		List<SmApplSupportDoc> licenseMandatoryDocs = new ArrayList<>();
		if (!EntityHelper.isCollectionEmpty(managedBean.getMandatoryLicenseDocs())) {
			for (SmLicenseInfoVO docVO : managedBean.getMandatoryLicenseDocs()) {
				licenseMandatoryDocs.addAll(docVO.getLicenseSuppDocs());
			}
		}
		if (SmApplicationUtil.isAllSuppDocAlreadyVerified(managedBean
				.getMandatoryDocs(), managedBean.getSmAppl()
				.getSupportingDocMode(), managedBean.isApproverUser())
				&& SmApplicationUtil.isAllSuppDocAlreadyVerified(
						managedBean.getNonMandatoryDocs(), managedBean
								.getSmAppl().getSupportingDocMode(),
						managedBean.isApproverUser()) && SmApplicationUtil.isAllSuppDocAlreadyVerified(
								licenseMandatoryDocs, managedBean
								.getSmAppl().getSupportingDocMode(),
						managedBean.isApproverUser())) {
			allowSiteVisit = true;
		}
		managedBean.setAllowedToSiteVisit(allowSiteVisit);
	}
	//#31043 END
	
	// #37779 - Start
	/**
	 * @param managedBean
	 *            - SmApplicationManagedBean object
	 * @return error message
	 */
	public static void checkBankAccountPurpose(
			SmApplicationManagedBean managedBean) {
		StringBuilder bf = new StringBuilder();
		int index = 1;

		SmAppl appl = managedBean.getSmAppl();

		for (SmSupplierBank bank : appl.getSmSupplierBanks()) {
			if (bank.getAccountPurpose().equalsIgnoreCase("O")) {
				if (index == 1) {
					bf.append("<br>" + index++ + ". "
							+ bank.getPmFinancialOrg().getFinOrgName() + " ("
							+ bank.getAccountNo() + ") " + "<br>");
					index++;
				} else if (index > 1) {
					bf.append(index++ + ". "
							+ bank.getPmFinancialOrg().getFinOrgName() + " ("
							+ bank.getAccountNo() + ") " + "<br>");
					index++;
				}

			}
		}

		if (index > 1) {
			if (managedBean.isSupplierUser()) {
				throw new EpException(MessageConst.ERROR_722710,
						new String[] { bf.toString() });
			} else if (managedBean.isApproverUser() || managedBean.isPoUser()) {
				throw new EpException(MessageConst.ERROR_722709,
						new String[] { bf.toString() });
			}
		}
	}
	// #37779 - End
	
	//#26238 - START
	/**
	 * Supplier with DA Suspend Registration / Cancel Registration / Blacklist Owner
	 *  can edit Bank Information only
	 * @param bean
	 * @param daList
	 */
	public static void enableBankSectionOnly(SmApplicationManagedBean bean, List<SmDisciplinaryAction> daList){
		if(bean.getEditVO() != null){
			if(!bean.isSupplierUser() || bean.getSmSupplier() == null){
				bean.getEditVO().setDisciplinary(false);
				return;
			}
			//39061
			List<SmDisciplinaryAction> activeDA = EntityHelper.isCollectionEmpty(daList) ?
					retrieveActiveSmDisciplinaryActionBySupplierForCMCD(bean.getSmSupplier()) : daList;
			
			if(hasCannotLoginDA(activeDA)){
				bean.getEditVO().setDisciplinary(true);
				return;
			}
		}
	}	
	
	/**
	 * @param daList
	 * @return true if suspend/cancel registration, blacklist owner DA exists
	 */
	public static boolean hasCannotLoginDA(List<SmDisciplinaryAction> daList){
		if(EntityHelper.isCollectionEmpty(daList)){ //#39217
			return false;
		}
		for(SmDisciplinaryAction da : daList){
			if(da.isSuspendRegistration() || da.isBlacklistOwner() || da.isCancelRegistration()){
				return true;
			}
		}
		return false;
	}
	//#26238 - END
	
	//#37605 START
	/*
	 * Validate string using ASCII character
	 */
	public static boolean convertToAscii(String input) {
        log.info("ASCII values for the string: \"" + input + "\"");
        boolean isValid = true;
        for (char character : input.toCharArray()) {
            int asciiValue = (int) character; 
            log.info("Character: '" + character + "' -> ASCII: " + asciiValue);
            if(asciiValue > 127){
            	isValid = false;
            }
        }
        return isValid;
    }
	
	/*
	 * Replace tab with a space
	 */
	public static String replaceTabWSpace(String input){
		String str = null;
		Map<String, String> documents = new HashMap<>();
        documents.put("input", input);

        log.info("Before Replacement:");
        for (Map.Entry<String, String> entry : documents.entrySet()) {
        	log.info(entry.getKey() + ": " + entry.getValue());
        }

        // Replace tabs with spaces in all values
        for (Map.Entry<String, String> entry : documents.entrySet()) {
            String value = entry.getValue();
            if (value != null) {
                // Replace tabs with 1 space
                value = value.replace("\t", " ");
                entry.setValue(value); 
            }
        }
        
        log.info("After Replacement:");
        for (Map.Entry<String, String> entry : documents.entrySet()) {
        	log.info(entry.getKey() + ": " + entry.getValue());
        	str = entry.getValue();
        }
        return str;
	}
	
	/**
	 * Builds a map of active categories for fast lookup.
	 * @param List
	 *            - SmSupplierCategory object
	 * @return category
	 */
	private static Map<Long, SmSupplierCategory> buildCategoryMap(List<SmSupplierCategory> activeCategoryList) {
	    Map<Long, SmSupplierCategory> categoryMap = new HashMap<>();
	    if (activeCategoryList != null && !activeCategoryList.isEmpty()) {
	        for (SmSupplierCategory newCat : activeCategoryList) {
	            if (newCat != null && newCat.getPmCategoryL3() != null) {
	                categoryMap.put(newCat.getPmCategoryL3().getCategoryL3Id(), newCat);
	            }
	        }
	    }
	    return categoryMap;
	}

	/**
	 * Updates the category description if a matching category exists.
	 * @param SmSupplierCategory, Map<Long, SmSupplierCategory>
	 *            
	 * set other description
	 */
	private static void updateCategoryDescription(SmSupplierCategory cat, Map<Long, SmSupplierCategory> categoryMap) {
	    if (cat.getPmCategoryL3() != null) {
	        SmSupplierCategory category = categoryMap.get(cat.getPmCategoryL3().getCategoryL3Id());
	        if (category != null && category.getPmCategoryL3().getCategoryL3Id().equals(cat.getPmCategoryL3().getCategoryL3Id())) {
	            cat.setOtherDesc(category.getOtherDesc());
	        }
	    }
	}
	
	/**
	 * Validates and cleans the special category description.
	 * @param cat
	 *            - SmSupplierCategory object
	 */
	private static void validateAndCleanCategory(SmSupplierCategory cat) {
	    if (cat.getOtherDesc() != null) {
	        if (!convertToAscii(cat.getOtherDesc())) {
	            throw new EpException(MessageConst.ERROR_760555);
	        }
	        cat.setOtherDesc(replaceTabWSpace(cat.getOtherDesc()));
	    }
	}
	//#37605 END
	
	// #37784 -STRAT
	/*
	 * Checks the application record status. If it is either DELETED or
	 * CANCELLED, redirects the user to the application error page with an
	 * appropriate message.
	 * 
	 * @param appl the application object to check
	 */
	public static void checkAndRedirectIfInvalid(SmAppl appl) {
		if (appl != null) {
			Long statusId = appl.getPmStatus().getStatusId();
			boolean isDeletedOrCancelled = appl.getRecordStatusFlg() == DbValueConst.RECORD_STATUS_DELETED
					|| appl.getRecordStatusFlg() == DbValueConst.RECORD_STATUS_CANCELLED;

			boolean isPendingVerificationStatus = SupplierManagementConst.PM_STATUS_PENDING_SUPPORTING_DOCUMENT_VERIFICATION
					.equals(String.valueOf(statusId));

			boolean isPendingPaymentStatus = statusId == SupplierManagementConst.PM_STATUS_PENDING_PROCESS_FEE;

			if (isDeletedOrCancelled || isPendingVerificationStatus || isPendingPaymentStatus) {
				SmCommonUtil.redirectToPage(SmApplicationUtil.getApplicationErrorPage(), MessageConst.ERROR_760600,
						null, FacesMessage.SEVERITY_INFO);
				return;
			}
		}
	}
	// #37784- END
	// 39392 - START
	/**
	 * Check Disciplinary Action for suspend reg, blacklisted owners/directors,
	 * and cancel reg.
	 * @param smAppl
	 */
	public static void checkOwnersDisciplinaryAction(SmAppl smAppl){
		if (smAppl == null){
			return;
		}
		
		RegistrationBackingBean.checkBannedOwners(smAppl.getSmSupplier().getSmSupplierOwners());
	}
	// 39392 - END
	
	//#26282 - Start
	/**
	 * 
	 * @param supplier
	 * @return resDTO
	 */
	public static List<SmDisciplinaryAction> retrieveActiveSmDisciplinaryActionBySupplierForCMCD(SmSupplier supplier) {
		SupplierDataMaintenanceReqDTO reqDTO = new SupplierDataMaintenanceReqDTO();
		reqDTO.setSupplier(supplier);

		SupplierDataMaintenanceResDTO resDTO = SmServiceLookup.getSupplierDataMaintenanceSO()
				.retrieveActiveDisciplinaryActionBySupplierForCMCD(reqDTO);

		return resDTO.getSmDisciplinaryActions();
	}
	//#26282 - End
}
