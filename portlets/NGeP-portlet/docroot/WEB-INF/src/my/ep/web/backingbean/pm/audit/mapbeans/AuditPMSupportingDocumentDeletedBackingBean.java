package my.ep.web.backingbean.pm.audit.mapbeans;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import my.ep.constant.pm.AuditConst;
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
	String roleCode;
	String docNo;
	BigInteger docId;
	BigInteger docGroupId;

	/**
	 * @param managedBeans
	 * @param roleCode
	 * @return
	 */
	public List<AuditPMSPDTO> deleteAppType(SupportDocumentManagedBean managedBeans, String roleCode) {
		ArrayList<AuditPMSPDTO> objList = new ArrayList<>();
		this.roleCode = roleCode;
		this.docNo = managedBeans.getSelectedDocument().getDocName();
		Integer rowIndex = managedBeans.getDeleteRowIndex();
		log.info("rowIndex ::: >>> ::: " + rowIndex);
		for (PmDocumentRestr pmDocumentRestr : managedBeans.getDeleteAppTypeRestrictionsAuditList()) {
			if (null != pmDocumentRestr.getAppTypeCode() && null != pmDocumentRestr.getAppTypeCode().getParameterId()
					&& pmDocumentRestr.getDocumentRestrId() != null) {
				this.docId = BigInteger.valueOf(pmDocumentRestr.getDocumentRestrId());
				this.docGroupId = BigInteger.valueOf(managedBeans.getSelectedDocument().getDocumentId());
				/*
				 * Restriction Document - AppTypeList - Application Type
				 */
				objList.add(detailObj(AuditPMSupplierMgtConfigConst.PM_AUD_SD_APPTYPE_FIELDLABELID, pmDocumentRestr
						.getAppTypeCode().getPmParameterDescsSpecCallOnly().get(1).getCodeDesc(), null,
						AuditConst.PM_AUD_ACTION_TYPE_DEL));

				/*
				 * Restriction Document - AppTypeList - Section : TextList -
				 * isAsscProjChanged()
				 */
				if (pmDocumentRestr.isAsscProjChanged()) {
					objList.add(detailObj(AuditPMSupplierMgtConfigConst.PM_AUD_SD_ASSCPROJECT_FIELDLABELID,
							yesNo(pmDocumentRestr.isAsscProjChanged()), null, AuditConst.PM_AUD_ACTION_TYPE_DEL));
				}

				/*
				 * Restriction Document - AppTypeList - Section : TextList -
				 * isCompanyInfoChanged
				 */
				if (pmDocumentRestr.isCompanyInfoChanged()) {
					objList.add(detailObj(AuditPMSupplierMgtConfigConst.PM_AUD_SD_COMPANYINFO_FIELDLABELID,
							yesNo(pmDocumentRestr.isCompanyInfoChanged()), null, AuditConst.PM_AUD_ACTION_TYPE_DEL));
				}

				/*
				 * Restriction Document - AppTypeList - Section : TextList -
				 * isPersonnelBranchChanged()
				 */
				if (pmDocumentRestr.isPersonnelBranchChanged()) {
					objList.add(detailObj(AuditPMSupplierMgtConfigConst.PM_AUD_SD_PERSONALBRANCH_FIELDLABELID,
							yesNo(pmDocumentRestr.isPersonnelBranchChanged()), null, AuditConst.PM_AUD_ACTION_TYPE_DEL));
				}

				/*
				 * Restriction Document - AppTypeList - Section : TextList -
				 * isBankChanged()
				 */
				if (pmDocumentRestr.isBankChanged()) {
					objList.add(detailObj(AuditPMSupplierMgtConfigConst.PM_AUD_SD_BANKINFO_FIELDLABELID,
							yesNo(pmDocumentRestr.isBankChanged()), null, AuditConst.PM_AUD_ACTION_TYPE_DEL));
				}

				/*
				 * Restriction Document - AppTypeList - Section : TextList -
				 * isCapitalEquityChanged()
				 */
				if (pmDocumentRestr.isCapitalEquityChanged()) {
					objList.add(detailObj(AuditPMSupplierMgtConfigConst.PM_AUD_SD_CAPITALEQUITY_FIELDLABELID,
							yesNo(pmDocumentRestr.isCapitalEquityChanged()), null, AuditConst.PM_AUD_ACTION_TYPE_DEL));
				}

				/* Facility Type : Home Office (Y/N) */
				objList.add(detailObj(AuditPMSupplierMgtConfigConst.PM_AUD_SD_FACILITYHOME_FIELDLABELID,
						yesNo(pmDocumentRestr.isHomeOffice()), null, AuditConst.PM_AUD_ACTION_TYPE_DEL));

				/* Facility Type : Business Office (Y/N) */
				objList.add(detailObj(AuditPMSupplierMgtConfigConst.PM_AUD_SD_FACILITYOFFICE_FIELDLABELID,
						yesNo(pmDocumentRestr.isBusinessOffice()), null, AuditConst.PM_AUD_ACTION_TYPE_DEL));
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
		this.roleCode = roleCode;
		this.docNo = managedBeans.getSelectedDocument().getDocName();
		for (PmDocumentRestr pmDocumentRestr : managedBeans.getDeleteComTypeRestrictionsAudit()) {
			if (null != pmDocumentRestr.getCompanyTypeCode() && null != pmDocumentRestr.getCompanyTypeCode().getParameterId()) {
				this.docId = BigInteger.valueOf(pmDocumentRestr.getDocumentRestrId());
				this.docGroupId = BigInteger.valueOf(managedBeans.getSelectedDocument().getDocumentId());
				objList.add(detailObj(AuditPMSupplierMgtConfigConst.PM_AUD_SD_COMPTYPE_FIELDLABELID, pmDocumentRestr
						.getCompanyTypeCode().getPmParameterDescsSpecCallOnly().get(1).getCodeDesc(), null,
						AuditConst.PM_AUD_ACTION_TYPE_DEL));
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
		ArrayList<AuditPMSPDTO> objList = new ArrayList<>();
		this.roleCode = roleCode;
		this.docNo = managedBeans.getSelectedDocument().getDocName();
		// deletesuppresrictOld << untick checkbox suppTypeRestrictionOld
		if (managedBeans.getDeleteSuppTypeRestrictionsAudit() != null) { // managedBeans.getSuppTypeRestrictionsOld()managedBeans.getComTypeRestrictionsOld()
			for (PmDocumentRestr pmDocumentRestr : managedBeans.getDeleteSuppTypeRestrictionsAudit()) {
				if (null != pmDocumentRestr.getSupplierTypeCode()
						&& null != pmDocumentRestr.getSupplierTypeCode().getParameterId()) {
					this.docId = BigInteger.valueOf(pmDocumentRestr.getDocumentRestrId());
					this.docGroupId = BigInteger.valueOf(managedBeans.getSelectedDocument().getDocumentId());
					objList.add(detailObj(AuditPMSupplierMgtConfigConst.PM_AUD_SD_SUPPTYPE_FIELDLABELID, pmDocumentRestr
							.getSupplierTypeCode().getPmParameterDescsSpecCallOnly().get(1).getCodeDesc(), null,
							AuditConst.PM_AUD_ACTION_TYPE_DEL));
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
		this.roleCode = roleCode;
		this.docNo = managedBeans.getSelectedDocument().getDocName();
		for (PmDocumentRestr pmDocumentRestr : managedBeans.getDeleteCategoryRestrictionsAudit()) {
			if (null != pmDocumentRestr.getPmCategoryL3().getCategoryL3Id()) {
				this.docId = BigInteger.valueOf(pmDocumentRestr.getDocumentRestrId());
				this.docGroupId = BigInteger.valueOf(managedBeans.getSelectedDocument().getDocumentId());
				objList.add(detailObj(
						AuditPMSupplierMgtConfigConst.PM_AUD_SD_CATEGORYCODETYPE_FIELDLABELID,
						pmDocumentRestr.getPmCategoryL3().getPmCategoryL2().getPmCategoryL1().getCategoryL1Code()
								.concat(pmDocumentRestr.getPmCategoryL3().getPmCategoryL2().getCategoryL2Code())
								.concat(pmDocumentRestr.getPmCategoryL3().getCategoryL3Code()), null,
						AuditConst.PM_AUD_ACTION_TYPE_DEL));

				objList.add(detailObj(AuditPMSupplierMgtConfigConst.PM_AUD_SD_CATEGORYMAINTYPE_FIELDLABELID, pmDocumentRestr
						.getPmCategoryL3().getPmCategoryL2().getPmCategoryL1().getCategoryName(), null,
						AuditConst.PM_AUD_ACTION_TYPE_DEL));

				objList.add(detailObj(AuditPMSupplierMgtConfigConst.PM_AUD_SD_CATEGORYSUBTYPE_FIELDLABELID, pmDocumentRestr
						.getPmCategoryL3().getPmCategoryL2().getCategoryName(), null, AuditConst.PM_AUD_ACTION_TYPE_DEL));

				objList.add(detailObj(AuditPMSupplierMgtConfigConst.PM_AUD_SD_CATEGORYSUBDIVTYPE_FIELDLABELID, pmDocumentRestr
						.getPmCategoryL3().getCategoryName(), null, AuditConst.PM_AUD_ACTION_TYPE_DEL));
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
		this.roleCode = roleCode;
		this.docNo = managedBeans.getSelectedDocument().getDocName();
		if (managedBeans.getDeleteCertBodyRestrictionsAudit() != null) {
			for (PmDocumentRestr pmDocumentRestr : managedBeans.getDeleteCertBodyRestrictionsAudit()) {
				if (null != pmDocumentRestr.getPmCertBody() && null != pmDocumentRestr.getPmCertBody().getCertBodyId()) {
					this.docId = BigInteger.valueOf(pmDocumentRestr.getDocumentRestrId());
					this.docGroupId = BigInteger.valueOf(managedBeans.getSelectedDocument().getDocumentId());
					objList.add(detailObj(AuditPMSupplierMgtConfigConst.PM_AUD_SD_CERTBODYTYPE_FIELDLABELID, pmDocumentRestr
							.getPmCertBody().getBodyName(), null, AuditConst.PM_AUD_ACTION_TYPE_DEL));
				}
			}
		}
		return objList;
	}

	private AuditPMSPDTO detailObj(BigInteger fieldLabelId, String oldValue, String newValue, String actionType) {
		AuditPMSPDTO dtlObj = new AuditPMSPDTO();
		dtlObj.setRoleCode(roleCode);
		dtlObj.setDocType(AuditPMSupplierMgtConfigConst.PM_AUD_SD);
		dtlObj.setPmParameterLvlId(AuditPMSupplierMgtConfigConst.PM_AUD_SD_PMPARAMETERLVLID);
		dtlObj.setDocNo(docNo);
		dtlObj.setLvlCode1(AuditPMSupplierMgtConfigConst.PM_LVL_CODE_1_SD);
		dtlObj.setLvlCode2(AuditPMSupplierMgtConfigConst.PM_LVL_CODE_2_SD);
		dtlObj.setLvlCode3(AuditPMSupplierMgtConfigConst.PM_LVL_CODE_3_SD);
		dtlObj.setDocId(docId);
		dtlObj.setDocGroupId(docGroupId);
		dtlObj.setFieldLabelCode(fieldLabelId);
		if (oldValue != null) {
			dtlObj.setOldValue(oldValue);
		}
		dtlObj.setNewValue(newValue);
		dtlObj.setActionType(actionType);
		return dtlObj;
	}

	private String yesNo(boolean yesNo) {
		return yesNo ? AuditPMSupplierMgtConfigConst.PM_AUD_YES : AuditPMSupplierMgtConfigConst.PM_AUD_NO;
	}
}