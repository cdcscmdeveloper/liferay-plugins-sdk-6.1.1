package my.ep.web.backingbean.pm.audit.mapbeans;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import my.ep.constant.pm.AuditPMSupplierMgtConfigConst;
import my.ep.dto.pm.common.audit.AuditPMSPDTO;
import my.ep.persistence.entity.pm.common.PmDocumentRestr;
import my.ep.web.managedbean.pm.common.supportdoc.SupportDocumentManagedBean;

/**
 * AuditPMSupportingDocumentBackingBean
 * 
 * @author normuliana #28895-35509
 * 
 */
public class AuditPMSupportingDocumentDeletedBackingBean {
	private static Logger log = Logger.getLogger(AuditPMSupportingDocumentDeletedBackingBean.class);

	/**
	 * @param managedBeans
	 * @param roleCode
	 * @return
	 */
	public List<AuditPMSPDTO> deleteAppType(SupportDocumentManagedBean managedBeans, String roleCode) {
		Integer rowIndex = managedBeans.getDeleteRowIndex();
		log.info("rowIndex ::: >>> ::: " + rowIndex);
		ArrayList<AuditPMSPDTO> objList = new ArrayList<>();
		for (PmDocumentRestr pmDocumentRestr : managedBeans.getDeleteAppTypeRestrictionsAuditList()) {
			if (null != pmDocumentRestr.getAppTypeCode().getParameterId() && pmDocumentRestr.getDocumentRestrId() != null) {
				/*
				 * Restriction Document - AppTypeList - Application Type
				 */
				AuditPMSPDTO detailObjApplicationTypeDeleted = new AuditPMSPDTO();
				detailObjApplicationTypeDeleted.setRoleCode(roleCode);
				detailObjApplicationTypeDeleted.setDocType(AuditPMSupplierMgtConfigConst.PM_AUD_SD);
				detailObjApplicationTypeDeleted.setPmParameterLvlId(AuditPMSupplierMgtConfigConst.PM_AUD_SD_PMPARAMETERLVLID);
				detailObjApplicationTypeDeleted.setDocNo(managedBeans.getSelectedDocument().getDocName());
				detailObjApplicationTypeDeleted.setLvlCode1(AuditPMSupplierMgtConfigConst.PM_LVL_CODE_1_SD);
				detailObjApplicationTypeDeleted.setLvlCode2(AuditPMSupplierMgtConfigConst.PM_LVL_CODE_2_SD);
				detailObjApplicationTypeDeleted.setLvlCode3(AuditPMSupplierMgtConfigConst.PM_LVL_CODE_3_SD);
				detailObjApplicationTypeDeleted.setDocId(BigInteger.valueOf(pmDocumentRestr.getDocumentRestrId()));
				detailObjApplicationTypeDeleted.setDocGroupId(BigInteger.valueOf(managedBeans.getSelectedDocument()
						.getDocumentId()));
				detailObjApplicationTypeDeleted.setFieldLabelCode(AuditPMSupplierMgtConfigConst.PM_AUD_SD_APPTYPE_FIELDLABELID);
				detailObjApplicationTypeDeleted.setOldValue(pmDocumentRestr.getAppTypeCode().getPmParameterDescsSpecCallOnly()
						.get(1).getCodeDesc());
				detailObjApplicationTypeDeleted.setActionType(AuditPMSupplierMgtConfigConst.PM_AUD_ACTION_TYPE_DEL);
				objList.add(detailObjApplicationTypeDeleted);

				/*
				 * Restriction Document - AppTypeList - Section : TextList -
				 * isAsscProjChanged()
				 */
				AuditPMSPDTO detailObjSectionIsAsscProjChangedDeleted = new AuditPMSPDTO();
				if (pmDocumentRestr.isAsscProjChanged()) {
					detailObjSectionIsAsscProjChangedDeleted.setRoleCode(roleCode);
					detailObjSectionIsAsscProjChangedDeleted.setDocType(AuditPMSupplierMgtConfigConst.PM_AUD_SD);
					detailObjSectionIsAsscProjChangedDeleted
							.setPmParameterLvlId(AuditPMSupplierMgtConfigConst.PM_AUD_SD_PMPARAMETERLVLID);
					detailObjSectionIsAsscProjChangedDeleted.setDocNo(managedBeans.getSelectedDocument().getDocName());
					detailObjSectionIsAsscProjChangedDeleted.setLvlCode1(AuditPMSupplierMgtConfigConst.PM_LVL_CODE_1_SD);
					detailObjSectionIsAsscProjChangedDeleted.setLvlCode2(AuditPMSupplierMgtConfigConst.PM_LVL_CODE_2_SD);
					detailObjSectionIsAsscProjChangedDeleted.setLvlCode3(AuditPMSupplierMgtConfigConst.PM_LVL_CODE_3_SD);
					detailObjSectionIsAsscProjChangedDeleted.setDocId(BigInteger.valueOf(pmDocumentRestr.getDocumentRestrId()));
					detailObjSectionIsAsscProjChangedDeleted.setDocGroupId(BigInteger.valueOf(managedBeans.getSelectedDocument()
							.getDocumentId()));
					detailObjSectionIsAsscProjChangedDeleted
							.setFieldLabelCode(AuditPMSupplierMgtConfigConst.PM_AUD_SD_ASSCPROJECT_FIELDLABELID);
					detailObjSectionIsAsscProjChangedDeleted.setOldValue(yesNo(pmDocumentRestr.isAsscProjChanged()));
					detailObjSectionIsAsscProjChangedDeleted.setActionType(AuditPMSupplierMgtConfigConst.PM_AUD_ACTION_TYPE_DEL);
					objList.add(detailObjSectionIsAsscProjChangedDeleted);
				}

				/*
				 * Restriction Document - AppTypeList - Section : TextList -
				 * isCompanyInfoChanged
				 */
				AuditPMSPDTO detailObjSectionIsCompanyInfoChangedDeleted = new AuditPMSPDTO();
				if (pmDocumentRestr.isCompanyInfoChanged()) {
					detailObjSectionIsCompanyInfoChangedDeleted.setRoleCode(roleCode);
					detailObjSectionIsCompanyInfoChangedDeleted.setDocType(AuditPMSupplierMgtConfigConst.PM_AUD_SD);
					detailObjSectionIsCompanyInfoChangedDeleted
							.setPmParameterLvlId(AuditPMSupplierMgtConfigConst.PM_AUD_SD_PMPARAMETERLVLID);
					detailObjSectionIsCompanyInfoChangedDeleted.setDocNo(managedBeans.getSelectedDocument().getDocName());
					detailObjSectionIsCompanyInfoChangedDeleted.setLvlCode1(AuditPMSupplierMgtConfigConst.PM_LVL_CODE_1_SD);
					detailObjSectionIsCompanyInfoChangedDeleted.setLvlCode2(AuditPMSupplierMgtConfigConst.PM_LVL_CODE_2_SD);
					detailObjSectionIsCompanyInfoChangedDeleted.setLvlCode3(AuditPMSupplierMgtConfigConst.PM_LVL_CODE_3_SD);
					detailObjSectionIsCompanyInfoChangedDeleted
							.setDocId(BigInteger.valueOf(pmDocumentRestr.getDocumentRestrId()));
					detailObjSectionIsCompanyInfoChangedDeleted.setDocGroupId(BigInteger.valueOf(managedBeans
							.getSelectedDocument().getDocumentId()));
					detailObjSectionIsCompanyInfoChangedDeleted
							.setFieldLabelCode(AuditPMSupplierMgtConfigConst.PM_AUD_SD_COMPANYINFO_FIELDLABELID);
					detailObjSectionIsCompanyInfoChangedDeleted.setOldValue(yesNo(pmDocumentRestr.isCompanyInfoChanged()));
					detailObjSectionIsCompanyInfoChangedDeleted
							.setActionType(AuditPMSupplierMgtConfigConst.PM_AUD_ACTION_TYPE_DEL);
					objList.add(detailObjSectionIsCompanyInfoChangedDeleted);
				}

				/*
				 * Restriction Document - AppTypeList - Section : TextList -
				 * isPersonnelBranchChanged()
				 */
				AuditPMSPDTO detailObjIsPersonnelBranchChangedDeleted = new AuditPMSPDTO();
				if (pmDocumentRestr.isPersonnelBranchChanged()) {
					detailObjIsPersonnelBranchChangedDeleted.setRoleCode(roleCode);
					detailObjIsPersonnelBranchChangedDeleted.setDocType(AuditPMSupplierMgtConfigConst.PM_AUD_SD);
					detailObjIsPersonnelBranchChangedDeleted
							.setPmParameterLvlId(AuditPMSupplierMgtConfigConst.PM_AUD_SD_PMPARAMETERLVLID);
					detailObjIsPersonnelBranchChangedDeleted.setDocNo(managedBeans.getSelectedDocument().getDocName());
					detailObjIsPersonnelBranchChangedDeleted.setLvlCode1(AuditPMSupplierMgtConfigConst.PM_LVL_CODE_1_SD);
					detailObjIsPersonnelBranchChangedDeleted.setLvlCode2(AuditPMSupplierMgtConfigConst.PM_LVL_CODE_2_SD);
					detailObjIsPersonnelBranchChangedDeleted.setLvlCode3(AuditPMSupplierMgtConfigConst.PM_LVL_CODE_3_SD);
					detailObjIsPersonnelBranchChangedDeleted.setDocId(BigInteger.valueOf(pmDocumentRestr.getDocumentRestrId()));
					detailObjIsPersonnelBranchChangedDeleted.setDocGroupId(BigInteger.valueOf(managedBeans.getSelectedDocument()
							.getDocumentId()));
					detailObjIsPersonnelBranchChangedDeleted
							.setFieldLabelCode(AuditPMSupplierMgtConfigConst.PM_AUD_SD_PERSONALBRANCH_FIELDLABELID);
					detailObjIsPersonnelBranchChangedDeleted.setOldValue(yesNo(pmDocumentRestr.isPersonnelBranchChanged()));
					detailObjIsPersonnelBranchChangedDeleted.setActionType(AuditPMSupplierMgtConfigConst.PM_AUD_ACTION_TYPE_DEL);
					objList.add(detailObjIsPersonnelBranchChangedDeleted);
				}

				/*
				 * Restriction Document - AppTypeList - Section : TextList -
				 * isBankChanged()
				 */
				AuditPMSPDTO isBankChangedDeleted = new AuditPMSPDTO();
				if (pmDocumentRestr.isBankChanged()) {
					isBankChangedDeleted.setRoleCode(roleCode);
					isBankChangedDeleted.setDocType(AuditPMSupplierMgtConfigConst.PM_AUD_SD);
					isBankChangedDeleted.setPmParameterLvlId(AuditPMSupplierMgtConfigConst.PM_AUD_SD_PMPARAMETERLVLID);
					isBankChangedDeleted.setDocNo(managedBeans.getSelectedDocument().getDocName());
					isBankChangedDeleted.setLvlCode1(AuditPMSupplierMgtConfigConst.PM_LVL_CODE_1_SD);
					isBankChangedDeleted.setLvlCode2(AuditPMSupplierMgtConfigConst.PM_LVL_CODE_2_SD);
					isBankChangedDeleted.setLvlCode3(AuditPMSupplierMgtConfigConst.PM_LVL_CODE_3_SD);
					isBankChangedDeleted.setDocId(BigInteger.valueOf(pmDocumentRestr.getDocumentRestrId()));
					isBankChangedDeleted.setDocGroupId(BigInteger.valueOf(managedBeans.getSelectedDocument().getDocumentId()));
					isBankChangedDeleted.setFieldLabelCode(AuditPMSupplierMgtConfigConst.PM_AUD_SD_BANKINFO_FIELDLABELID);
					isBankChangedDeleted.setOldValue(yesNo(pmDocumentRestr.isBankChanged()));
					isBankChangedDeleted.setActionType(AuditPMSupplierMgtConfigConst.PM_AUD_ACTION_TYPE_DEL);
					objList.add(isBankChangedDeleted);
				}

				/*
				 * Restriction Document - AppTypeList - Section : TextList -
				 * isCapitalEquityChanged()
				 */
				AuditPMSPDTO detailObjIsCapitalEquityChangedDeleted = new AuditPMSPDTO();
				if (pmDocumentRestr.isCapitalEquityChanged()) {
					detailObjIsCapitalEquityChangedDeleted.setRoleCode(roleCode);
					detailObjIsCapitalEquityChangedDeleted.setDocType(AuditPMSupplierMgtConfigConst.PM_AUD_SD);
					detailObjIsCapitalEquityChangedDeleted
							.setPmParameterLvlId(AuditPMSupplierMgtConfigConst.PM_AUD_SD_PMPARAMETERLVLID);
					detailObjIsCapitalEquityChangedDeleted.setDocNo(managedBeans.getSelectedDocument().getDocName());
					detailObjIsCapitalEquityChangedDeleted.setLvlCode1(AuditPMSupplierMgtConfigConst.PM_LVL_CODE_1_SD);
					detailObjIsCapitalEquityChangedDeleted.setLvlCode2(AuditPMSupplierMgtConfigConst.PM_LVL_CODE_2_SD);
					detailObjIsCapitalEquityChangedDeleted.setLvlCode3(AuditPMSupplierMgtConfigConst.PM_LVL_CODE_3_SD);
					detailObjIsCapitalEquityChangedDeleted.setDocId(BigInteger.valueOf(pmDocumentRestr.getDocumentRestrId()));
					detailObjIsCapitalEquityChangedDeleted.setDocGroupId(BigInteger.valueOf(managedBeans.getSelectedDocument()
							.getDocumentId()));
					detailObjIsCapitalEquityChangedDeleted
							.setFieldLabelCode(AuditPMSupplierMgtConfigConst.PM_AUD_SD_CAPITALEQUITY_FIELDLABELID);
					detailObjIsCapitalEquityChangedDeleted.setOldValue(yesNo(pmDocumentRestr.isCapitalEquityChanged()));
					detailObjIsCapitalEquityChangedDeleted.setActionType(AuditPMSupplierMgtConfigConst.PM_AUD_ACTION_TYPE_DEL);
					objList.add(detailObjIsCapitalEquityChangedDeleted);
				}

				/* Facility Type : Home Office (Y/N) */
				AuditPMSPDTO detailObjFacilityTypeHomeDeleted = new AuditPMSPDTO();
				detailObjFacilityTypeHomeDeleted.setRoleCode(roleCode);
				detailObjFacilityTypeHomeDeleted.setDocType(AuditPMSupplierMgtConfigConst.PM_AUD_SD);
				detailObjFacilityTypeHomeDeleted.setPmParameterLvlId(AuditPMSupplierMgtConfigConst.PM_AUD_SD_PMPARAMETERLVLID);
				detailObjFacilityTypeHomeDeleted.setDocNo(managedBeans.getSelectedDocument().getDocName());
				detailObjFacilityTypeHomeDeleted.setLvlCode1(AuditPMSupplierMgtConfigConst.PM_LVL_CODE_1_SD);
				detailObjFacilityTypeHomeDeleted.setLvlCode2(AuditPMSupplierMgtConfigConst.PM_LVL_CODE_2_SD);
				detailObjFacilityTypeHomeDeleted.setLvlCode3(AuditPMSupplierMgtConfigConst.PM_LVL_CODE_3_SD);
				detailObjFacilityTypeHomeDeleted.setDocId(BigInteger.valueOf(pmDocumentRestr.getDocumentRestrId()));
				detailObjFacilityTypeHomeDeleted.setDocGroupId(BigInteger.valueOf(managedBeans.getSelectedDocument()
						.getDocumentId()));
				detailObjFacilityTypeHomeDeleted
						.setFieldLabelCode(AuditPMSupplierMgtConfigConst.PM_AUD_SD_FACILITYHOME_FIELDLABELID);
				detailObjFacilityTypeHomeDeleted.setOldValue(yesNo(pmDocumentRestr.isHomeOffice()));
				detailObjFacilityTypeHomeDeleted.setActionType(AuditPMSupplierMgtConfigConst.PM_AUD_ACTION_TYPE_DEL);
				objList.add(detailObjFacilityTypeHomeDeleted);

				/* Facility Type : Business Office (Y/N) */
				AuditPMSPDTO detailObjFacilityTypeOfficeDeleted = new AuditPMSPDTO();
				detailObjFacilityTypeOfficeDeleted.setRoleCode(roleCode);
				detailObjFacilityTypeOfficeDeleted.setDocType(AuditPMSupplierMgtConfigConst.PM_AUD_SD);
				detailObjFacilityTypeOfficeDeleted.setPmParameterLvlId(AuditPMSupplierMgtConfigConst.PM_AUD_SD_PMPARAMETERLVLID);
				detailObjFacilityTypeOfficeDeleted.setDocNo(managedBeans.getSelectedDocument().getDocName());
				detailObjFacilityTypeOfficeDeleted.setLvlCode1(AuditPMSupplierMgtConfigConst.PM_LVL_CODE_1_SD);
				detailObjFacilityTypeOfficeDeleted.setLvlCode2(AuditPMSupplierMgtConfigConst.PM_LVL_CODE_2_SD);
				detailObjFacilityTypeOfficeDeleted.setLvlCode3(AuditPMSupplierMgtConfigConst.PM_LVL_CODE_3_SD);
				detailObjFacilityTypeOfficeDeleted.setDocId(BigInteger.valueOf(pmDocumentRestr.getDocumentRestrId()));
				detailObjFacilityTypeOfficeDeleted.setDocGroupId(BigInteger.valueOf(managedBeans.getSelectedDocument()
						.getDocumentId()));
				detailObjFacilityTypeOfficeDeleted
						.setFieldLabelCode(AuditPMSupplierMgtConfigConst.PM_AUD_SD_FACILITYOFFICE_FIELDLABELID);
				detailObjFacilityTypeOfficeDeleted.setOldValue(yesNo(pmDocumentRestr.isBusinessOffice()));
				detailObjFacilityTypeOfficeDeleted.setActionType(AuditPMSupplierMgtConfigConst.PM_AUD_ACTION_TYPE_DEL);
				objList.add(detailObjFacilityTypeOfficeDeleted);
			}
		}

		return objList;
	}

	/**
	 * @param managedBeans
	 * @param roleCode
	 * @return
	 */
	public List<AuditPMSPDTO> deleteComType(SupportDocumentManagedBean managedBeans, String roleCode) {
		ArrayList<AuditPMSPDTO> objList = new ArrayList<>();
		for (PmDocumentRestr pmDocumentRestr : managedBeans.getDeleteComTypeRestrictionsAudit()) {
			if (null != pmDocumentRestr.getCompanyTypeCode().getParameterId()) {
				AuditPMSPDTO detailObjCompTypeList = new AuditPMSPDTO();
				detailObjCompTypeList.setRoleCode(roleCode);
				detailObjCompTypeList.setDocType(AuditPMSupplierMgtConfigConst.PM_AUD_SD);
				detailObjCompTypeList.setPmParameterLvlId(AuditPMSupplierMgtConfigConst.PM_AUD_SD_PMPARAMETERLVLID);
				detailObjCompTypeList.setDocNo(managedBeans.getSelectedDocument().getDocName());
				detailObjCompTypeList.setLvlCode1(AuditPMSupplierMgtConfigConst.PM_LVL_CODE_1_SD);
				detailObjCompTypeList.setLvlCode2(AuditPMSupplierMgtConfigConst.PM_LVL_CODE_2_SD);
				detailObjCompTypeList.setLvlCode3(AuditPMSupplierMgtConfigConst.PM_LVL_CODE_3_SD);
				detailObjCompTypeList.setDocId(BigInteger.valueOf(pmDocumentRestr.getDocumentRestrId()));
				detailObjCompTypeList.setDocGroupId(BigInteger.valueOf(managedBeans.getSelectedDocument().getDocumentId()));
				detailObjCompTypeList.setFieldLabelCode(AuditPMSupplierMgtConfigConst.PM_AUD_SD_COMPTYPE_FIELDLABELID);
				detailObjCompTypeList.setOldValue(pmDocumentRestr.getCompanyTypeCode().getPmParameterDescsSpecCallOnly().get(1)
						.getCodeDesc());
				detailObjCompTypeList.setActionType(AuditPMSupplierMgtConfigConst.PM_AUD_ACTION_TYPE_DEL);
				objList.add(detailObjCompTypeList);
			}
		}

		return objList;
	}

	/**
	 * @param managedBeans
	 * @param roleCode
	 * @return
	 */
	public List<AuditPMSPDTO> deleteSuppType(SupportDocumentManagedBean managedBeans, String roleCode) {
		ArrayList<AuditPMSPDTO> objList = new ArrayList<>(); // deletesuppresrictOld
																// <<< untick
																// checkbox
																// suppTypeRestrictionOld
		if (managedBeans.getDeleteSuppTypeRestrictionsAudit() != null) { // managedBeans.getSuppTypeRestrictionsOld()
																			// //
			// managedBeans.getComTypeRestrictionsOld()
			for (PmDocumentRestr pmDocumentRestr : managedBeans.getDeleteSuppTypeRestrictionsAudit()) {
				if (null != pmDocumentRestr.getSupplierTypeCode()
						&& null != pmDocumentRestr.getSupplierTypeCode().getParameterId()) {
					AuditPMSPDTO detailObjSuppTypeList = new AuditPMSPDTO();
					detailObjSuppTypeList.setRoleCode(roleCode);
					detailObjSuppTypeList.setDocType(AuditPMSupplierMgtConfigConst.PM_AUD_SD);
					detailObjSuppTypeList.setPmParameterLvlId(AuditPMSupplierMgtConfigConst.PM_AUD_SD_PMPARAMETERLVLID);
					detailObjSuppTypeList.setDocNo(managedBeans.getSelectedDocument().getDocName());
					detailObjSuppTypeList.setLvlCode1(AuditPMSupplierMgtConfigConst.PM_LVL_CODE_1_SD);
					detailObjSuppTypeList.setLvlCode2(AuditPMSupplierMgtConfigConst.PM_LVL_CODE_2_SD);
					detailObjSuppTypeList.setLvlCode3(AuditPMSupplierMgtConfigConst.PM_LVL_CODE_3_SD);
					detailObjSuppTypeList.setDocId(BigInteger.valueOf(pmDocumentRestr.getDocumentRestrId()));
					detailObjSuppTypeList.setDocGroupId(BigInteger.valueOf(managedBeans.getSelectedDocument().getDocumentId()));
					detailObjSuppTypeList.setFieldLabelCode(AuditPMSupplierMgtConfigConst.PM_AUD_SD_SUPPTYPE_FIELDLABELID);
					detailObjSuppTypeList.setOldValue(pmDocumentRestr.getSupplierTypeCode().getPmParameterDescsSpecCallOnly()
							.get(1).getCodeDesc());
					detailObjSuppTypeList.setActionType(AuditPMSupplierMgtConfigConst.PM_AUD_ACTION_TYPE_DEL);
					objList.add(detailObjSuppTypeList);
				}
			}
		}

		return objList;
	}

	/**
	 * @param managedBeans
	 * @param roleCode
	 * @return
	 */
	public List<AuditPMSPDTO> deleteCategoryType(SupportDocumentManagedBean managedBeans, String roleCode) {
		ArrayList<AuditPMSPDTO> objList = new ArrayList<>();
		for (PmDocumentRestr pmDocumentRestr : managedBeans.getDeleteCategoryRestrictionsAudit()) {
			if (null != pmDocumentRestr.getPmCategoryL3().getCategoryL3Id()) {
				AuditPMSPDTO detailObjCategoryCodeTypeList = new AuditPMSPDTO();
				detailObjCategoryCodeTypeList.setRoleCode(roleCode);
				detailObjCategoryCodeTypeList.setDocType(AuditPMSupplierMgtConfigConst.PM_AUD_SD);
				detailObjCategoryCodeTypeList.setPmParameterLvlId(AuditPMSupplierMgtConfigConst.PM_AUD_SD_PMPARAMETERLVLID);
				detailObjCategoryCodeTypeList.setDocNo(managedBeans.getSelectedDocument().getDocName());
				detailObjCategoryCodeTypeList.setLvlCode1(AuditPMSupplierMgtConfigConst.PM_LVL_CODE_1_SD);
				detailObjCategoryCodeTypeList.setLvlCode2(AuditPMSupplierMgtConfigConst.PM_LVL_CODE_2_SD);
				detailObjCategoryCodeTypeList.setLvlCode3(AuditPMSupplierMgtConfigConst.PM_LVL_CODE_3_SD);
				detailObjCategoryCodeTypeList.setDocId(BigInteger.valueOf(pmDocumentRestr.getDocumentRestrId()));
				detailObjCategoryCodeTypeList.setDocGroupId(BigInteger
						.valueOf(managedBeans.getSelectedDocument().getDocumentId()));
				detailObjCategoryCodeTypeList
						.setFieldLabelCode(AuditPMSupplierMgtConfigConst.PM_AUD_SD_CATEGORYCODETYPE_FIELDLABELID);
				detailObjCategoryCodeTypeList.setOldValue(pmDocumentRestr.getPmCategoryL3().getPmCategoryL2().getPmCategoryL1()
						.getCategoryL1Code().concat(pmDocumentRestr.getPmCategoryL3().getPmCategoryL2().getCategoryL2Code())
						.concat(pmDocumentRestr.getPmCategoryL3().getCategoryL3Code()));
				detailObjCategoryCodeTypeList.setActionType(AuditPMSupplierMgtConfigConst.PM_AUD_ACTION_TYPE_DEL);
				objList.add(detailObjCategoryCodeTypeList);

				AuditPMSPDTO detailObjMainCategoryTypeList = new AuditPMSPDTO();
				detailObjMainCategoryTypeList.setRoleCode(roleCode);
				detailObjMainCategoryTypeList.setDocType(AuditPMSupplierMgtConfigConst.PM_AUD_SD);
				detailObjMainCategoryTypeList.setPmParameterLvlId(AuditPMSupplierMgtConfigConst.PM_AUD_SD_PMPARAMETERLVLID);
				detailObjMainCategoryTypeList.setDocNo(managedBeans.getSelectedDocument().getDocName());
				detailObjMainCategoryTypeList.setLvlCode1(AuditPMSupplierMgtConfigConst.PM_LVL_CODE_1_SD);
				detailObjMainCategoryTypeList.setLvlCode2(AuditPMSupplierMgtConfigConst.PM_LVL_CODE_2_SD);
				detailObjMainCategoryTypeList.setLvlCode3(AuditPMSupplierMgtConfigConst.PM_LVL_CODE_3_SD);
				detailObjMainCategoryTypeList.setDocId(BigInteger.valueOf(pmDocumentRestr.getDocumentRestrId()));
				detailObjMainCategoryTypeList.setDocGroupId(BigInteger
						.valueOf(managedBeans.getSelectedDocument().getDocumentId()));
				detailObjMainCategoryTypeList
						.setFieldLabelCode(AuditPMSupplierMgtConfigConst.PM_AUD_SD_CATEGORYMAINTYPE_FIELDLABELID);
				detailObjMainCategoryTypeList.setOldValue(pmDocumentRestr.getPmCategoryL3().getPmCategoryL2().getPmCategoryL1()
						.getCategoryName());
				detailObjMainCategoryTypeList.setActionType(AuditPMSupplierMgtConfigConst.PM_AUD_ACTION_TYPE_DEL);
				objList.add(detailObjMainCategoryTypeList);

				AuditPMSPDTO detailObjSubCategoryTypeList = new AuditPMSPDTO();
				detailObjSubCategoryTypeList.setRoleCode(roleCode);
				detailObjSubCategoryTypeList.setDocType(AuditPMSupplierMgtConfigConst.PM_AUD_SD);
				detailObjSubCategoryTypeList.setPmParameterLvlId(AuditPMSupplierMgtConfigConst.PM_AUD_SD_PMPARAMETERLVLID);
				detailObjSubCategoryTypeList.setDocNo(managedBeans.getSelectedDocument().getDocName());
				detailObjSubCategoryTypeList.setLvlCode1(AuditPMSupplierMgtConfigConst.PM_LVL_CODE_1_SD);
				detailObjSubCategoryTypeList.setLvlCode2(AuditPMSupplierMgtConfigConst.PM_LVL_CODE_2_SD);
				detailObjSubCategoryTypeList.setLvlCode3(AuditPMSupplierMgtConfigConst.PM_LVL_CODE_3_SD);
				detailObjSubCategoryTypeList.setDocId(BigInteger.valueOf(pmDocumentRestr.getDocumentRestrId()));
				detailObjSubCategoryTypeList
						.setDocGroupId(BigInteger.valueOf(managedBeans.getSelectedDocument().getDocumentId()));
				detailObjSubCategoryTypeList
						.setFieldLabelCode(AuditPMSupplierMgtConfigConst.PM_AUD_SD_CATEGORYSUBTYPE_FIELDLABELID);
				detailObjSubCategoryTypeList.setOldValue(pmDocumentRestr.getPmCategoryL3().getPmCategoryL2().getCategoryName());
				detailObjSubCategoryTypeList.setActionType(AuditPMSupplierMgtConfigConst.PM_AUD_ACTION_TYPE_DEL);
				objList.add(detailObjSubCategoryTypeList);

				AuditPMSPDTO detailObjSubDivCategoryTypeList = new AuditPMSPDTO();
				detailObjSubDivCategoryTypeList.setRoleCode(roleCode);
				detailObjSubDivCategoryTypeList.setDocType(AuditPMSupplierMgtConfigConst.PM_AUD_SD);
				detailObjSubDivCategoryTypeList.setPmParameterLvlId(AuditPMSupplierMgtConfigConst.PM_AUD_SD_PMPARAMETERLVLID);
				detailObjSubDivCategoryTypeList.setDocNo(managedBeans.getSelectedDocument().getDocName());
				detailObjSubDivCategoryTypeList.setLvlCode1(AuditPMSupplierMgtConfigConst.PM_LVL_CODE_1_SD);
				detailObjSubDivCategoryTypeList.setLvlCode2(AuditPMSupplierMgtConfigConst.PM_LVL_CODE_2_SD);
				detailObjSubDivCategoryTypeList.setLvlCode3(AuditPMSupplierMgtConfigConst.PM_LVL_CODE_3_SD);
				detailObjSubDivCategoryTypeList.setDocId(BigInteger.valueOf(pmDocumentRestr.getDocumentRestrId()));
				detailObjSubDivCategoryTypeList.setDocGroupId(BigInteger.valueOf(managedBeans.getSelectedDocument()
						.getDocumentId()));
				detailObjSubDivCategoryTypeList
						.setFieldLabelCode(AuditPMSupplierMgtConfigConst.PM_AUD_SD_CATEGORYSUBDIVTYPE_FIELDLABELID);
				detailObjSubDivCategoryTypeList.setOldValue(pmDocumentRestr.getPmCategoryL3().getCategoryName());
				detailObjSubDivCategoryTypeList.setActionType(AuditPMSupplierMgtConfigConst.PM_AUD_ACTION_TYPE_DEL);
				objList.add(detailObjSubDivCategoryTypeList);
			}
		}
		return objList;
	}

	/**
	 * @param managedBeans
	 * @param roleCode
	 * @return
	 */
	public List<AuditPMSPDTO> deleteCertBodyType(SupportDocumentManagedBean managedBeans, String roleCode) {
		ArrayList<AuditPMSPDTO> objList = new ArrayList<>();
		if (managedBeans.getDeleteCertBodyRestrictionsAudit() != null) {
			for (PmDocumentRestr pmDocumentRestr : managedBeans.getDeleteCertBodyRestrictionsAudit()) {
				if (null != pmDocumentRestr.getPmCertBody().getCertBodyId()) {
					AuditPMSPDTO detailObjCerBodyTypeList = new AuditPMSPDTO();
					detailObjCerBodyTypeList.setRoleCode(roleCode);
					detailObjCerBodyTypeList.setDocType(AuditPMSupplierMgtConfigConst.PM_AUD_SD);
					detailObjCerBodyTypeList.setPmParameterLvlId(AuditPMSupplierMgtConfigConst.PM_AUD_SD_PMPARAMETERLVLID);
					detailObjCerBodyTypeList.setDocNo(managedBeans.getSelectedDocument().getDocName());
					detailObjCerBodyTypeList.setLvlCode1(AuditPMSupplierMgtConfigConst.PM_LVL_CODE_1_SD);
					detailObjCerBodyTypeList.setLvlCode2(AuditPMSupplierMgtConfigConst.PM_LVL_CODE_2_SD);
					detailObjCerBodyTypeList.setLvlCode3(AuditPMSupplierMgtConfigConst.PM_LVL_CODE_3_SD);
					detailObjCerBodyTypeList.setDocId(BigInteger.valueOf(pmDocumentRestr.getDocumentRestrId()));
					detailObjCerBodyTypeList
							.setDocGroupId(BigInteger.valueOf(managedBeans.getSelectedDocument().getDocumentId()));
					detailObjCerBodyTypeList.setFieldLabelCode(AuditPMSupplierMgtConfigConst.PM_AUD_SD_CERTBODYTYPE_FIELDLABELID);
					detailObjCerBodyTypeList.setOldValue(pmDocumentRestr.getPmCertBody().getBodyName());
					detailObjCerBodyTypeList.setActionType(AuditPMSupplierMgtConfigConst.PM_AUD_ACTION_TYPE_DEL);
					objList.add(detailObjCerBodyTypeList);
				}
			}
		}

		return objList;
	}

	private String yesNo(boolean yesNo) {
		return yesNo ? AuditPMSupplierMgtConfigConst.PM_AUD_YES : AuditPMSupplierMgtConfigConst.PM_AUD_NO;
	}
}