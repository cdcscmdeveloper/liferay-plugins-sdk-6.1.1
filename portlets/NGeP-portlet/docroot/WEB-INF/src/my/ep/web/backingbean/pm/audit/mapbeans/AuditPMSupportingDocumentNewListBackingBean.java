package my.ep.web.backingbean.pm.audit.mapbeans;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import my.ep.constant.pm.AuditPMSupplierMgtConfigConst;
import my.ep.dto.pm.common.audit.AuditPMSPDTO;
import my.ep.persistence.constant.DbValueConst;
import my.ep.persistence.entity.pm.common.PmDocumentRestr;
import my.ep.web.managedbean.pm.common.supportdoc.SupportDocumentManagedBean;

/**
 * AuditPMSupportingDocumentBackingBean
 * 
 * @author normuliana #28895-35509
 * 
 */
public class AuditPMSupportingDocumentNewListBackingBean {
	/**
	 * @param managedBeans
	 * @param roleCode
	 * @param updated
	 * @return
	 */
	public List<AuditPMSPDTO> updatedNewCreateRowAppTypeTabList(SupportDocumentManagedBean managedBeans, String roleCode,
			boolean updated) {
		ArrayList<AuditPMSPDTO> objList = new ArrayList<>();

		for (PmDocumentRestr pmDocumentRestr : managedBeans.getAppTypeRestrictionsNew()) {
			/*
			 * Restriction Document - AppTypeList - Application Type
			 */
			AuditPMSPDTO detailObjApplicationTypeList = new AuditPMSPDTO();
			detailObjApplicationTypeList.setRoleCode(roleCode);
			detailObjApplicationTypeList.setDocType(AuditPMSupplierMgtConfigConst.PM_AUD_SD);
			detailObjApplicationTypeList.setPmParameterLvlId(AuditPMSupplierMgtConfigConst.PM_AUD_SD_PMPARAMETERLVLID);
			detailObjApplicationTypeList.setDocNo(managedBeans.getSelectedDocument().getDocName());
			detailObjApplicationTypeList.setLvlCode1(AuditPMSupplierMgtConfigConst.PM_LVL_CODE_1_SD);
			detailObjApplicationTypeList.setLvlCode2(AuditPMSupplierMgtConfigConst.PM_LVL_CODE_2_SD);
			detailObjApplicationTypeList.setLvlCode3(AuditPMSupplierMgtConfigConst.PM_LVL_CODE_3_SD);
			detailObjApplicationTypeList.setDocId(BigInteger.valueOf(pmDocumentRestr.getAppTypeCode().getParameterId()));
			detailObjApplicationTypeList.setDocGroupId(BigInteger.valueOf(managedBeans.getSelectedDocument().getDocumentId()));
			detailObjApplicationTypeList.setFieldLabelCode(AuditPMSupplierMgtConfigConst.PM_AUD_SD_APPTYPE_FIELDLABELID);
			detailObjApplicationTypeList.setNewValue(pmDocumentRestr.getAppTypeCode().getPmParameterDescsSpecCallOnly().get(1)
					.getCodeDesc());
			detailObjApplicationTypeList.setActionType(actionType(updated));
			objList.add(detailObjApplicationTypeList);

			/*
			 * Restriction Document - AppTypeList - Section : TextList -
			 * isAsscProjChanged()
			 */
			AuditPMSPDTO detailObjSectionIsAsscProjChangedList = new AuditPMSPDTO();
			if (pmDocumentRestr.isAsscProjChanged()) {
				detailObjSectionIsAsscProjChangedList.setRoleCode(roleCode);
				detailObjSectionIsAsscProjChangedList.setDocType(AuditPMSupplierMgtConfigConst.PM_AUD_SD);
				detailObjSectionIsAsscProjChangedList
						.setPmParameterLvlId(AuditPMSupplierMgtConfigConst.PM_AUD_SD_PMPARAMETERLVLID);
				detailObjSectionIsAsscProjChangedList.setDocNo(managedBeans.getSelectedDocument().getDocName());
				detailObjSectionIsAsscProjChangedList.setLvlCode1(AuditPMSupplierMgtConfigConst.PM_LVL_CODE_1_SD);
				detailObjSectionIsAsscProjChangedList.setLvlCode2(AuditPMSupplierMgtConfigConst.PM_LVL_CODE_2_SD);
				detailObjSectionIsAsscProjChangedList.setLvlCode3(AuditPMSupplierMgtConfigConst.PM_LVL_CODE_3_SD);
				detailObjSectionIsAsscProjChangedList.setDocId(BigInteger.valueOf(pmDocumentRestr.getDocumentRestrId()));
				detailObjSectionIsAsscProjChangedList.setDocGroupId(BigInteger.valueOf(managedBeans.getSelectedDocument()
						.getDocumentId()));
				detailObjSectionIsAsscProjChangedList
						.setFieldLabelCode(AuditPMSupplierMgtConfigConst.PM_AUD_SD_ASSCPROJECT_FIELDLABELID);
				detailObjSectionIsAsscProjChangedList.setNewValue(yesNo(pmDocumentRestr.isAsscProjChanged()));
				detailObjSectionIsAsscProjChangedList.setActionType(actionType(updated));
				objList.add(detailObjSectionIsAsscProjChangedList);
			}

			/*
			 * Restriction Document - AppTypeList - Section : TextList -
			 * isCompanyInfoChanged
			 */
			AuditPMSPDTO detailObjSectionIsCompanyInfoChangedList = new AuditPMSPDTO();
			if (pmDocumentRestr.isCompanyInfoChanged()) {
				detailObjSectionIsCompanyInfoChangedList.setRoleCode(roleCode);
				detailObjSectionIsCompanyInfoChangedList.setDocType(AuditPMSupplierMgtConfigConst.PM_AUD_SD);
				detailObjSectionIsCompanyInfoChangedList
						.setPmParameterLvlId(AuditPMSupplierMgtConfigConst.PM_AUD_SD_PMPARAMETERLVLID);
				detailObjSectionIsCompanyInfoChangedList.setDocNo(managedBeans.getSelectedDocument().getDocName());
				detailObjSectionIsCompanyInfoChangedList.setLvlCode1(AuditPMSupplierMgtConfigConst.PM_LVL_CODE_1_SD);
				detailObjSectionIsCompanyInfoChangedList.setLvlCode2(AuditPMSupplierMgtConfigConst.PM_LVL_CODE_2_SD);
				detailObjSectionIsCompanyInfoChangedList.setLvlCode3(AuditPMSupplierMgtConfigConst.PM_LVL_CODE_3_SD);
				detailObjSectionIsCompanyInfoChangedList.setDocId(BigInteger.valueOf(pmDocumentRestr.getDocumentRestrId()));
				detailObjSectionIsCompanyInfoChangedList.setDocGroupId(BigInteger.valueOf(managedBeans.getSelectedDocument()
						.getDocumentId()));
				detailObjSectionIsCompanyInfoChangedList
						.setFieldLabelCode(AuditPMSupplierMgtConfigConst.PM_AUD_SD_COMPANYINFO_FIELDLABELID);
				detailObjSectionIsCompanyInfoChangedList.setNewValue(yesNo(pmDocumentRestr.isCompanyInfoChanged()));
				detailObjSectionIsCompanyInfoChangedList.setActionType(actionType(updated));
				objList.add(detailObjSectionIsCompanyInfoChangedList);
			}

			/*
			 * Restriction Document - AppTypeList - Section : TextList -
			 * isPersonnelBranchChanged()
			 */
			AuditPMSPDTO detailObjIsPersonnelBranchChangedList = new AuditPMSPDTO();
			if (pmDocumentRestr.isPersonnelBranchChanged()) {
				detailObjIsPersonnelBranchChangedList.setRoleCode(roleCode);
				detailObjIsPersonnelBranchChangedList.setDocType(AuditPMSupplierMgtConfigConst.PM_AUD_SD);
				detailObjIsPersonnelBranchChangedList
						.setPmParameterLvlId(AuditPMSupplierMgtConfigConst.PM_AUD_SD_PMPARAMETERLVLID);
				detailObjIsPersonnelBranchChangedList.setDocNo(managedBeans.getSelectedDocument().getDocName());
				detailObjIsPersonnelBranchChangedList.setLvlCode1(AuditPMSupplierMgtConfigConst.PM_LVL_CODE_1_SD);
				detailObjIsPersonnelBranchChangedList.setLvlCode2(AuditPMSupplierMgtConfigConst.PM_LVL_CODE_2_SD);
				detailObjIsPersonnelBranchChangedList.setLvlCode3(AuditPMSupplierMgtConfigConst.PM_LVL_CODE_3_SD);
				detailObjIsPersonnelBranchChangedList.setDocId(BigInteger.valueOf(pmDocumentRestr.getDocumentRestrId()));
				detailObjIsPersonnelBranchChangedList.setDocGroupId(BigInteger.valueOf(managedBeans.getSelectedDocument()
						.getDocumentId()));
				detailObjIsPersonnelBranchChangedList
						.setFieldLabelCode(AuditPMSupplierMgtConfigConst.PM_AUD_SD_PERSONALBRANCH_FIELDLABELID);
				detailObjIsPersonnelBranchChangedList.setNewValue(yesNo(pmDocumentRestr.isPersonnelBranchChanged()));
				detailObjIsPersonnelBranchChangedList.setActionType(actionType(updated));
				objList.add(detailObjIsPersonnelBranchChangedList);
			}

			/*
			 * Restriction Document - AppTypeList - Section : TextList -
			 * isBankChanged()
			 */
			AuditPMSPDTO isBankChangedList = new AuditPMSPDTO();
			if (pmDocumentRestr.isBankChanged()) {
				isBankChangedList.setRoleCode(roleCode);
				isBankChangedList.setDocType(AuditPMSupplierMgtConfigConst.PM_AUD_SD);
				isBankChangedList.setPmParameterLvlId(AuditPMSupplierMgtConfigConst.PM_AUD_SD_PMPARAMETERLVLID);
				isBankChangedList.setDocNo(managedBeans.getSelectedDocument().getDocName());
				isBankChangedList.setLvlCode1(AuditPMSupplierMgtConfigConst.PM_LVL_CODE_1_SD);
				isBankChangedList.setLvlCode2(AuditPMSupplierMgtConfigConst.PM_LVL_CODE_2_SD);
				isBankChangedList.setLvlCode3(AuditPMSupplierMgtConfigConst.PM_LVL_CODE_3_SD);
				isBankChangedList.setDocId(BigInteger.valueOf(pmDocumentRestr.getDocumentRestrId()));
				isBankChangedList.setDocGroupId(BigInteger.valueOf(managedBeans.getSelectedDocument().getDocumentId()));
				isBankChangedList.setFieldLabelCode(AuditPMSupplierMgtConfigConst.PM_AUD_SD_BANKINFO_FIELDLABELID);
				isBankChangedList.setNewValue(yesNo(pmDocumentRestr.isBankChanged()));
				isBankChangedList.setActionType(actionType(updated));
				objList.add(isBankChangedList);
			}

			/*
			 * Restriction Document - AppTypeList - Section : TextList -
			 * isCapitalEquityChanged()
			 */
			AuditPMSPDTO detailObjIsCapitalEquityChangedList = new AuditPMSPDTO();
			if (pmDocumentRestr.isCapitalEquityChanged()) {
				detailObjIsCapitalEquityChangedList.setRoleCode(roleCode);
				detailObjIsCapitalEquityChangedList.setDocType(AuditPMSupplierMgtConfigConst.PM_AUD_SD);
				detailObjIsCapitalEquityChangedList.setPmParameterLvlId(AuditPMSupplierMgtConfigConst.PM_AUD_SD_PMPARAMETERLVLID);
				detailObjIsCapitalEquityChangedList.setDocNo(managedBeans.getSelectedDocument().getDocName());
				detailObjIsCapitalEquityChangedList.setLvlCode1(AuditPMSupplierMgtConfigConst.PM_LVL_CODE_1_SD);
				detailObjIsCapitalEquityChangedList.setLvlCode2(AuditPMSupplierMgtConfigConst.PM_LVL_CODE_2_SD);
				detailObjIsCapitalEquityChangedList.setLvlCode3(AuditPMSupplierMgtConfigConst.PM_LVL_CODE_3_SD);
				detailObjIsCapitalEquityChangedList.setDocId(BigInteger.valueOf(pmDocumentRestr.getDocumentRestrId()));
				detailObjIsCapitalEquityChangedList.setDocGroupId(BigInteger.valueOf(managedBeans.getSelectedDocument()
						.getDocumentId()));
				detailObjIsCapitalEquityChangedList
						.setFieldLabelCode(AuditPMSupplierMgtConfigConst.PM_AUD_SD_CAPITALEQUITY_FIELDLABELID);
				detailObjIsCapitalEquityChangedList.setNewValue(yesNo(pmDocumentRestr.isCapitalEquityChanged()));
				detailObjIsCapitalEquityChangedList.setActionType(actionType(updated));
				objList.add(detailObjIsCapitalEquityChangedList);
			}

			/* Facility Type : Home Office (Y/N) */
			AuditPMSPDTO detailObjFacilityTypeHomeList = new AuditPMSPDTO();
			detailObjFacilityTypeHomeList.setRoleCode(roleCode);
			detailObjFacilityTypeHomeList.setDocType(AuditPMSupplierMgtConfigConst.PM_AUD_SD);
			detailObjFacilityTypeHomeList.setPmParameterLvlId(AuditPMSupplierMgtConfigConst.PM_AUD_SD_PMPARAMETERLVLID);
			detailObjFacilityTypeHomeList.setDocNo(managedBeans.getSelectedDocument().getDocName());
			detailObjFacilityTypeHomeList.setLvlCode1(AuditPMSupplierMgtConfigConst.PM_LVL_CODE_1_SD);
			detailObjFacilityTypeHomeList.setLvlCode2(AuditPMSupplierMgtConfigConst.PM_LVL_CODE_2_SD);
			detailObjFacilityTypeHomeList.setLvlCode3(AuditPMSupplierMgtConfigConst.PM_LVL_CODE_3_SD);
			detailObjFacilityTypeHomeList.setDocId(BigInteger.valueOf(pmDocumentRestr.getDocumentRestrId()));
			detailObjFacilityTypeHomeList.setDocGroupId(BigInteger.valueOf(managedBeans.getSelectedDocument().getDocumentId()));
			detailObjFacilityTypeHomeList.setFieldLabelCode(AuditPMSupplierMgtConfigConst.PM_AUD_SD_FACILITYHOME_FIELDLABELID);
			detailObjFacilityTypeHomeList.setNewValue(yesNo(pmDocumentRestr.isHomeOffice()));
			detailObjFacilityTypeHomeList.setActionType(actionType(updated));
			objList.add(detailObjFacilityTypeHomeList);

			/* Facility Type : Business Office (Y/N) */
			AuditPMSPDTO detailObjFacilityTypeOfficeList = new AuditPMSPDTO();
			detailObjFacilityTypeOfficeList.setRoleCode(roleCode);
			detailObjFacilityTypeOfficeList.setDocType(AuditPMSupplierMgtConfigConst.PM_AUD_SD);
			detailObjFacilityTypeOfficeList.setPmParameterLvlId(AuditPMSupplierMgtConfigConst.PM_AUD_SD_PMPARAMETERLVLID);
			detailObjFacilityTypeOfficeList.setDocNo(managedBeans.getSelectedDocument().getDocName());
			detailObjFacilityTypeOfficeList.setLvlCode1(AuditPMSupplierMgtConfigConst.PM_LVL_CODE_1_SD);
			detailObjFacilityTypeOfficeList.setLvlCode2(AuditPMSupplierMgtConfigConst.PM_LVL_CODE_2_SD);
			detailObjFacilityTypeOfficeList.setLvlCode3(AuditPMSupplierMgtConfigConst.PM_LVL_CODE_3_SD);
			detailObjFacilityTypeOfficeList.setDocId(BigInteger.valueOf(pmDocumentRestr.getDocumentRestrId()));
			detailObjFacilityTypeOfficeList.setDocGroupId(BigInteger.valueOf(managedBeans.getSelectedDocument().getDocumentId()));
			detailObjFacilityTypeOfficeList
					.setFieldLabelCode(AuditPMSupplierMgtConfigConst.PM_AUD_SD_FACILITYOFFICE_FIELDLABELID);
			detailObjFacilityTypeOfficeList.setNewValue(yesNo(pmDocumentRestr.isBusinessOffice()));
			detailObjFacilityTypeOfficeList.setActionType(actionType(updated));
			objList.add(detailObjFacilityTypeOfficeList);
		}

		managedBeans.setAppTypeRestrictionsNew(null);

		return objList;
	}

	/**
	 * @param managedBeans
	 * @param roleCode
	 * @param updated
	 * @return
	 */
	public List<AuditPMSPDTO> updatedNewCreateRowComTypeTabList(SupportDocumentManagedBean managedBeans, String roleCode,
			boolean updated) {
		ArrayList<AuditPMSPDTO> objList = new ArrayList<>();
		for (PmDocumentRestr pmDocumentRestr : managedBeans.getNewCompanyCodeTypeAuditList()) {
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
			detailObjCompTypeList.setNewValue(pmDocumentRestr.getCompanyTypeCode().getPmParameterDescsSpecCallOnly().get(1)
					.getCodeDesc());
			detailObjCompTypeList.setActionType(actionType(updated));
			objList.add(detailObjCompTypeList);
		}

		managedBeans.setComTypeRestrictionsNew(null);

		return objList;
	}

	/**
	 * @param managedBeans
	 * @param roleCode
	 * @param updated
	 * @return
	 */
	public List<AuditPMSPDTO> updatedNewCreateRowSuppTypeTabList(SupportDocumentManagedBean managedBeans, String roleCode,
			boolean updated) {
		ArrayList<AuditPMSPDTO> objList = new ArrayList<>();
		for (PmDocumentRestr pmDocumentRestr : managedBeans.getSuppTypeRestrictionsNew()) {
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
			detailObjSuppTypeList.setNewValue(pmDocumentRestr.getSupplierTypeCode().getPmParameterDescsSpecCallOnly().get(1)
					.getCodeDesc());
			detailObjSuppTypeList.setActionType(actionType(updated));
			objList.add(detailObjSuppTypeList);
		}

		managedBeans.setSuppTypeRestrictionsNew(null);

		return objList;
	}

	/**
	 * @param managedBeans
	 * @param roleCode
	 * @param updated
	 * @return
	 */
	public List<AuditPMSPDTO> updatedNewCreateRowCategoryTypeTabList(SupportDocumentManagedBean managedBeans, String roleCode,
			boolean updated) {
		ArrayList<AuditPMSPDTO> objList = new ArrayList<>();
		for (PmDocumentRestr pmDocumentRestr : managedBeans.getNewCategoryL3AuditList()) {
			if (pmDocumentRestr.isBrandNew() && pmDocumentRestr.getRecordStatusFlg().equals(DbValueConst.RECORD_STATUS_ACTIVE)) {
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
				detailObjCategoryCodeTypeList.setNewValue(pmDocumentRestr.getPmCategoryL3().getPmCategoryL2().getPmCategoryL1()
						.getCategoryL1Code().concat(pmDocumentRestr.getPmCategoryL3().getPmCategoryL2().getCategoryL2Code())
						.concat(pmDocumentRestr.getPmCategoryL3().getCategoryL3Code()));
				detailObjCategoryCodeTypeList.setActionType(actionType(updated));
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
				detailObjMainCategoryTypeList.setNewValue(pmDocumentRestr.getPmCategoryL3().getPmCategoryL2().getPmCategoryL1()
						.getCategoryName());
				detailObjMainCategoryTypeList.setActionType(actionType(updated));
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
				detailObjSubCategoryTypeList.setNewValue(pmDocumentRestr.getPmCategoryL3().getPmCategoryL2().getCategoryName());
				detailObjSubCategoryTypeList.setActionType(actionType(updated));
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
				detailObjSubDivCategoryTypeList.setNewValue(pmDocumentRestr.getPmCategoryL3().getCategoryName());
				detailObjSubDivCategoryTypeList.setActionType(actionType(updated));
				objList.add(detailObjSubDivCategoryTypeList);
			}
		}

		managedBeans.setCategoryRestrictionsNew(null);

		return objList;
	}

	/**
	 * @param managedBeans
	 * @param roleCode
	 * @param updated
	 * @return
	 */
	public List<AuditPMSPDTO> updatedNewCreateRowCertBodyTypeTabList(SupportDocumentManagedBean managedBeans, String roleCode,
			boolean updated) {
		ArrayList<AuditPMSPDTO> objList = new ArrayList<>();
		for (PmDocumentRestr pmDocumentRestr : managedBeans.getCertBodyRestrictionsNew()) {
			AuditPMSPDTO detailObjCerBodyTypeList = new AuditPMSPDTO();
			detailObjCerBodyTypeList.setRoleCode(roleCode);
			detailObjCerBodyTypeList.setDocType(AuditPMSupplierMgtConfigConst.PM_AUD_SD);
			detailObjCerBodyTypeList.setPmParameterLvlId(AuditPMSupplierMgtConfigConst.PM_AUD_SD_PMPARAMETERLVLID);
			detailObjCerBodyTypeList.setDocNo(managedBeans.getSelectedDocument().getDocName());
			detailObjCerBodyTypeList.setLvlCode1(AuditPMSupplierMgtConfigConst.PM_LVL_CODE_1_SD);
			detailObjCerBodyTypeList.setLvlCode2(AuditPMSupplierMgtConfigConst.PM_LVL_CODE_2_SD);
			detailObjCerBodyTypeList.setLvlCode3(AuditPMSupplierMgtConfigConst.PM_LVL_CODE_3_SD);
			detailObjCerBodyTypeList.setDocId(BigInteger.valueOf(pmDocumentRestr.getDocumentRestrId()));
			detailObjCerBodyTypeList.setDocGroupId(BigInteger.valueOf(managedBeans.getSelectedDocument().getDocumentId()));
			detailObjCerBodyTypeList.setFieldLabelCode(AuditPMSupplierMgtConfigConst.PM_AUD_SD_CERTBODYTYPE_FIELDLABELID);
			detailObjCerBodyTypeList.setNewValue(pmDocumentRestr.getPmCertBody().getDescription());
			detailObjCerBodyTypeList.setActionType(actionType(updated));
			objList.add(detailObjCerBodyTypeList);
		}

		managedBeans.setCertBodyRestrictionsNew(null);

		return objList;
	}

	private String actionType(boolean actionTypeUpdated) {
		return actionTypeUpdated ? AuditPMSupplierMgtConfigConst.PM_AUD_ACTION_TYPE_UPD
				: AuditPMSupplierMgtConfigConst.PM_AUD_ACTION_TYPE_INS;
	}

	private String yesNo(boolean yesNo) {
		return yesNo ? AuditPMSupplierMgtConfigConst.PM_AUD_YES : AuditPMSupplierMgtConfigConst.PM_AUD_NO;
	}
}