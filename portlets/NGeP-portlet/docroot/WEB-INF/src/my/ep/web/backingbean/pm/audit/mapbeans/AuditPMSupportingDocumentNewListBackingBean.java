package my.ep.web.backingbean.pm.audit.mapbeans;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import my.ep.constant.pm.AuditPMSupplierMgtConfigConst;
import my.ep.dto.pm.common.audit.AuditPMReqDTO;
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
	private static Logger log = Logger.getLogger(AuditPMSupportingDocumentNewListBackingBean.class);
	AuditPMReqDTO auditPMReqDTO = new AuditPMReqDTO();
	ArrayList<AuditPMSPDTO> objList = new ArrayList<>();
	String roleCode;
	String docNo;
	String docType;
	BigInteger docId;
	BigInteger docGroupId;

	/**
	 * @param managedBeans
	 * @param roleCode
	 * @param updated
	 * @return
	 */
	public List<AuditPMSPDTO> updatedNewCreateRowAppTypeTabList(SupportDocumentManagedBean managedBeans, String roleCode,
			boolean updated) {
		this.roleCode = roleCode;
		this.docNo = managedBeans.getSelectedDocument().getDisplaySeq();
		this.docType = managedBeans.getSelectedDocument().getDocName();
		for (PmDocumentRestr pmDocumentRestr : managedBeans.getAppTypeRestrictionsNew()) {
			/*
			 * Restriction Document - AppTypeList - Application Type #14
			 */
			this.docId = BigInteger.valueOf(pmDocumentRestr.getAppTypeCode().getParameterId());
			this.docGroupId = BigInteger.valueOf(managedBeans.getSelectedDocument().getDocumentId());
			detailObj(AuditPMSupplierMgtConfigConst.PM_AUD_SD_APPTYPE_FIELDLABELID, null, pmDocumentRestr.getAppTypeCode()
					.getPmParameterDescsSpecCallOnly().get(1).getCodeDesc(), actionType(updated));
			/*
			 * Restriction Document - AppTypeList - Section : TextList -
			 * isCompanyInfoChanged #15
			 */
			if (pmDocumentRestr.isCompanyInfoChanged()) {
				this.docId = BigInteger.valueOf(pmDocumentRestr.getDocumentRestrId());
				this.docGroupId = BigInteger.valueOf(managedBeans.getSelectedDocument().getDocumentId());
				detailObj(AuditPMSupplierMgtConfigConst.PM_AUD_SD_COMPANYINFO_FIELDLABELID, null,
						yesNo(pmDocumentRestr.isCompanyInfoChanged()), actionType(updated));
			}
			/*
			 * Restriction Document - AppTypeList - Section : TextList -
			 * isAsscProjChanged() #16
			 */
			if (pmDocumentRestr.isAsscProjChanged()) {
				this.docId = BigInteger.valueOf(pmDocumentRestr.getDocumentRestrId());
				this.docGroupId = BigInteger.valueOf(managedBeans.getSelectedDocument().getDocumentId());
				detailObj(AuditPMSupplierMgtConfigConst.PM_AUD_SD_ASSCPROJECT_FIELDLABELID, null,
						yesNo(pmDocumentRestr.isAsscProjChanged()), actionType(updated));
			}
			/*
			 * Restriction Document - AppTypeList - Section : TextList -
			 * isPersonnelBranchChanged() #17
			 */
			if (pmDocumentRestr.isPersonnelBranchChanged()) {
				this.docId = BigInteger.valueOf(pmDocumentRestr.getDocumentRestrId());
				this.docGroupId = BigInteger.valueOf(managedBeans.getSelectedDocument().getDocumentId());
				detailObj(AuditPMSupplierMgtConfigConst.PM_AUD_SD_PERSONALBRANCH_FIELDLABELID, null,
						yesNo(pmDocumentRestr.isPersonnelBranchChanged()), actionType(updated));
			}
			/*
			 * Restriction Document - AppTypeList - Section : TextList -
			 * isCapitalEquityChanged() #18
			 */
			if (pmDocumentRestr.isCapitalEquityChanged()) {
				this.docId = BigInteger.valueOf(pmDocumentRestr.getDocumentRestrId());
				this.docGroupId = BigInteger.valueOf(managedBeans.getSelectedDocument().getDocumentId());
				detailObj(AuditPMSupplierMgtConfigConst.PM_AUD_SD_CAPITALEQUITY_FIELDLABELID, null,
						yesNo(pmDocumentRestr.isCapitalEquityChanged()), actionType(updated));
			}
			/*
			 * Restriction Document - AppTypeList - Section : TextList -
			 * isBankChanged() #19
			 */
			if (pmDocumentRestr.isBankChanged()) {
				this.docId = BigInteger.valueOf(pmDocumentRestr.getDocumentRestrId());
				this.docGroupId = BigInteger.valueOf(managedBeans.getSelectedDocument().getDocumentId());
				detailObj(AuditPMSupplierMgtConfigConst.PM_AUD_SD_BANKINFO_FIELDLABELID, null,
						yesNo(pmDocumentRestr.isBankChanged()), actionType(updated));
			}
			/* Facility Type : Home Office (Y/N) #20 */
			this.docId = BigInteger.valueOf(pmDocumentRestr.getDocumentRestrId());
			this.docGroupId = BigInteger.valueOf(managedBeans.getSelectedDocument().getDocumentId());
			detailObj(AuditPMSupplierMgtConfigConst.PM_AUD_SD_FACILITYHOME_FIELDLABELID, null,
					yesNo(pmDocumentRestr.isHomeOffice()), actionType(updated));

			/* Facility Type : Business Office (Y/N) #21 */
			detailObj(AuditPMSupplierMgtConfigConst.PM_AUD_SD_FACILITYOFFICE_FIELDLABELID, null,
					yesNo(pmDocumentRestr.isBusinessOffice()), actionType(updated));
		}

		managedBeans.setAppTypeRestrictionsNew(null);

		return objList;
	}

	/**
	 * @param managedBeans
	 * @param roleCode
	 * @param updated
	 * @return #22
	 */
	public List<AuditPMSPDTO> updatedNewCreateRowComTypeTabList(SupportDocumentManagedBean managedBeans, String roleCode,
			boolean updated) {
		this.roleCode = roleCode;
		this.docNo = managedBeans.getSelectedDocument().getDisplaySeq();
		this.docType = managedBeans.getSelectedDocument().getDocName();
		if (null != managedBeans.getNewCompanyCodeTypeAuditList() && !managedBeans.getNewCompanyCodeTypeAuditList().isEmpty()) {
			log.info("managedBeans.getNewCompanyCodeTypeAuditList()_AAA#&##& ::: "
					+ managedBeans.getNewCompanyCodeTypeAuditList().size());
			for (PmDocumentRestr pmDocumentRestr : managedBeans.getNewCompanyCodeTypeAuditList()) {
				log.info("pmDocumentRestr.getDocumentRestrId()_BBB#&##& ::: " + pmDocumentRestr.getDocumentRestrId());
			this.docId = BigInteger.valueOf(pmDocumentRestr.getDocumentRestrId());
				log.info("managedBeans.getSelectedDocument().getDocumentId()_CCC#&##& ::: "
						+ managedBeans.getSelectedDocument().getDocumentId());
			this.docGroupId = BigInteger.valueOf(managedBeans.getSelectedDocument().getDocumentId());
				System.out
						.println("pmDocumentRestr.getCompanyTypeCode().getPmParameterDescsSpecCallOnly().get(1).getCodeDesc()_DDD#&##& ::: "
								+ pmDocumentRestr.getCompanyTypeCode().getPmParameterDescsSpecCallOnly().get(1).getCodeDesc());
			detailObj(AuditPMSupplierMgtConfigConst.PM_AUD_SD_COMPTYPE_FIELDLABELID, null, pmDocumentRestr.getCompanyTypeCode()
					.getPmParameterDescsSpecCallOnly().get(1).getCodeDesc(), actionType(updated));
		}

		managedBeans.setComTypeRestrictionsNew(null);
		}
		return objList;
	}

	/**
	 * @param managedBeans
	 * @param roleCode
	 * @param updated
	 * @return #23
	 */
	public List<AuditPMSPDTO> updatedNewCreateRowSuppTypeTabList(SupportDocumentManagedBean managedBeans, String roleCode,
			boolean updated) {
		this.roleCode = roleCode;
		this.docNo = managedBeans.getSelectedDocument().getDisplaySeq();
		this.docType = managedBeans.getSelectedDocument().getDocName();
		for (PmDocumentRestr pmDocumentRestr : managedBeans.getSuppTypeRestrictionsNew()) {
			this.docId = BigInteger.valueOf(pmDocumentRestr.getDocumentRestrId());
			this.docGroupId = BigInteger.valueOf(managedBeans.getSelectedDocument().getDocumentId());
			detailObj(AuditPMSupplierMgtConfigConst.PM_AUD_SD_SUPPTYPE_FIELDLABELID, null, pmDocumentRestr.getSupplierTypeCode()
					.getPmParameterDescsSpecCallOnly().get(1).getCodeDesc(), actionType(updated));
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
		this.roleCode = roleCode;
		this.docNo = managedBeans.getSelectedDocument().getDisplaySeq();
		this.docType = managedBeans.getSelectedDocument().getDocName();
		this.docType = managedBeans.getSelectedDocument().getDocName();
		for (PmDocumentRestr pmDocumentRestr : managedBeans.getNewCategoryL3AuditList()) {
			if (pmDocumentRestr.isBrandNew() && pmDocumentRestr.getRecordStatusFlg().equals(DbValueConst.RECORD_STATUS_ACTIVE)) {
				this.docId = BigInteger.valueOf(pmDocumentRestr.getDocumentRestrId());
				this.docGroupId = BigInteger.valueOf(managedBeans.getSelectedDocument().getDocumentId());
				/* 24 */
				detailObj(
						AuditPMSupplierMgtConfigConst.PM_AUD_SD_CATEGORYCODETYPE_FIELDLABELID,
						null,
						pmDocumentRestr.getPmCategoryL3().getPmCategoryL2().getPmCategoryL1().getCategoryL1Code()
								.concat(pmDocumentRestr.getPmCategoryL3().getPmCategoryL2().getCategoryL2Code())
								.concat(pmDocumentRestr.getPmCategoryL3().getCategoryL3Code()), actionType(updated));
				/* 25 */
				detailObj(AuditPMSupplierMgtConfigConst.PM_AUD_SD_CATEGORYMAINTYPE_FIELDLABELID, null, pmDocumentRestr
						.getPmCategoryL3().getPmCategoryL2().getCategoryName(), actionType(updated));
				/* 26 */
				detailObj(AuditPMSupplierMgtConfigConst.PM_AUD_SD_CATEGORYSUBTYPE_FIELDLABELID, null, pmDocumentRestr
						.getPmCategoryL3().getPmCategoryL2().getPmCategoryL1().getCategoryName(), actionType(updated));
				/* 27 */
				detailObj(AuditPMSupplierMgtConfigConst.PM_AUD_SD_CATEGORYSUBDIVTYPE_FIELDLABELID, null, pmDocumentRestr
						.getPmCategoryL3().getCategoryName(), actionType(updated));
			}
		}

		managedBeans.setCategoryRestrictionsNew(null);

		return objList;
	}

	/**
	 * @param managedBeans
	 * @param roleCode
	 * @param updated
	 * @return #28
	 */
	public List<AuditPMSPDTO> updatedNewCreateRowCertBodyTypeTabList(SupportDocumentManagedBean managedBeans, String roleCode,
			boolean updated) {
		this.roleCode = roleCode;
		this.docNo = managedBeans.getSelectedDocument().getDisplaySeq();
		this.docType = managedBeans.getSelectedDocument().getDocName();
		for (PmDocumentRestr pmDocumentRestr : managedBeans.getCertBodyRestrictionsNew()) {
			this.docId = BigInteger.valueOf(pmDocumentRestr.getDocumentRestrId());
			this.docGroupId = BigInteger.valueOf(managedBeans.getSelectedDocument().getDocumentId());
			detailObj(AuditPMSupplierMgtConfigConst.PM_AUD_SD_CERTBODYTYPE_FIELDLABELID, null, pmDocumentRestr.getPmCertBody()
					.getBodyName(), actionType(updated));
		}

		managedBeans.setCertBodyRestrictionsNew(null);

		return objList;
	}

	private void detailObj(BigInteger fieldLabelId, String oldValue, String newValue, String actionType) {
		AuditPMSPDTO dtlObj = new AuditPMSPDTO();
		dtlObj.setRoleCode(roleCode);
		dtlObj.setDocType(docType);
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
		objList.add(dtlObj);
	}

	private String actionType(boolean actionTypeUpdated) {
		return actionTypeUpdated ? AuditPMSupplierMgtConfigConst.PM_AUD_ACTION_TYPE_INS
				: AuditPMSupplierMgtConfigConst.PM_AUD_ACTION_TYPE_INS;
	}

	private String yesNo(boolean yesNo) {
		return yesNo ? AuditPMSupplierMgtConfigConst.PM_AUD_YES : AuditPMSupplierMgtConfigConst.PM_AUD_NO;
	}
}