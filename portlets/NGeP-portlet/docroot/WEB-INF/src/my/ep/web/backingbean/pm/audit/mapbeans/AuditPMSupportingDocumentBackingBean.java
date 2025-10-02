package my.ep.web.backingbean.pm.audit.mapbeans;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;

import my.ep.constant.pm.AuditPMSupplierMgtConfigConst;
import my.ep.dto.pm.common.audit.AuditPMReqDTO;
import my.ep.dto.pm.common.audit.AuditPMSPDTO;
import my.ep.persistence.constant.DbValueConst;
import my.ep.persistence.entity.pm.common.PmDocumentRestr;
import my.ep.web.backingbean.pm.audit.AuditPMBackingBeanTemplate;
import my.ep.web.backingbean.pm.audit.role.AuditPMRoleBackingBean;
import my.ep.web.backingbean.pm.common.supportdoc.SupportDocumentBackingBean;
import my.ep.web.managedbean.BaseManagedBean;
import my.ep.web.managedbean.pm.common.supportdoc.SupportDocumentManagedBean;

/**
 * AuditPMSupportingDocumentBackingBean
 * 
 * @author normuliana #28895-35509
 * 
 */
public class AuditPMSupportingDocumentBackingBean extends AuditPMBackingBeanTemplate {

	private static final long serialVersionUID = 1L;
	private static Logger log = Logger.getLogger(AuditPMSupportingDocumentBackingBean.class);
	private static final Set<String> HANDLED_CHECKBOXES;
	AuditPMReqDTO auditPMReqDTO = new AuditPMReqDTO();
	ArrayList<AuditPMSPDTO> objList = new ArrayList<>();
	boolean updated = false;
	String roleCode;
	String docNo;
	String docType;
	BigInteger docId;
	BigInteger docGroupId;

	private List<PmDocumentRestr> appTypeRestrictionsUpdated = new ArrayList<>();

	private List<PmDocumentRestr> appTypeRestrictionsNew = new ArrayList<>();
	private List<PmDocumentRestr> suppTypeRestrictionsNew = new ArrayList<>();
	private List<PmDocumentRestr> categoryRestrictionsNew = new ArrayList<>();
	private List<PmDocumentRestr> certBodyRestrictionsNew = new ArrayList<>();

	private List<PmDocumentRestr> appTypeRestrictionsOld = new ArrayList<>();
	private List<PmDocumentRestr> comTypeRestrictionsOld = new ArrayList<>();
	private List<PmDocumentRestr> suppTypeRestrictionsOld = new ArrayList<>();
	private List<PmDocumentRestr> categoryRestrictionsOld = new ArrayList<>();
	private List<PmDocumentRestr> certBodyRestrictionsOld = new ArrayList<>();

	@Override
	public String getRoleCode() {
		AuditPMRoleBackingBean auditPMRoleBackingBean = new AuditPMRoleBackingBean();
		return auditPMRoleBackingBean.roleSupportingDoc();
	}

	/**
	 * @param managedBeans
	 * @param roleCode
	 * @return
	 */
	@Override
	public AuditPMReqDTO insert(BaseManagedBean managedBean, String roleCode) {
		SupportDocumentManagedBean managedBeans = (SupportDocumentManagedBean) managedBean;
		this.roleCode = roleCode;
		this.docNo = managedBeans.getSelectedDocument().getDisplaySeq();
		this.docType = managedBeans.getSelectedDocument().getDocName();
		if (null != managedBeans.getSelectedDocument() && null != managedBeans.getSelectedDocumentOld()) {
			updated = true;
			// /* Display priority **/
			if (!managedBeans.getSelectedDocument().getDisplaySeq()
					.equalsIgnoreCase(managedBeans.getSelectedDocumentOld().getDisplaySeq())) {
				this.docGroupId = BigInteger.valueOf(managedBeans.getSelectedDocument().getDocumentId());
				this.docId = BigInteger.valueOf(managedBeans.getSelectedDocument().getDocumentId());

				detailObj(AuditPMSupplierMgtConfigConst.PM_AUD_SD_DISPLAY_PRIORITY_FIELDLABELID, managedBeans
						.getSelectedDocumentOld().getDisplaySeq(), managedBeans.getSelectedDocument().getDisplaySeq(),
						AuditPMSupplierMgtConfigConst.PM_AUD_ACTION_TYPE_UPD);
			}
			// /* detailObjStatus */
			if (managedBeans.getSelectedDocument().getRecordStatusFlg() != managedBeans.getSelectedDocumentOld()
					.getRecordStatusFlg()) {
				this.docGroupId = BigInteger.valueOf(managedBeans.getSelectedDocument().getDocumentId());
				this.docId = BigInteger.valueOf(managedBeans.getSelectedDocument().getDocumentId());

				detailObj(
						AuditPMSupplierMgtConfigConst.PM_AUD_SD_STATUS_FIELDLABELID,
						managedBeans.getSelectedDocumentOld().getRecordStatusFlg() == 1 ? AuditPMSupplierMgtConfigConst.PM_AUD_ACTIVE
								: AuditPMSupplierMgtConfigConst.PM_AUD_INACTIVE,
						managedBeans.getSelectedDocument().getRecordStatusFlg() == 1 ? AuditPMSupplierMgtConfigConst.PM_AUD_ACTIVE
								: AuditPMSupplierMgtConfigConst.PM_AUD_INACTIVE,
						AuditPMSupplierMgtConfigConst.PM_AUD_ACTION_TYPE_UPD);
			}
			getOldValList(managedBeans);

			handleCheckboxUpdateAdded(managedBeans, updated);

			deleted(managedBeans);

			/* Create new row list in update/create mode */
			updateCreateTablist(managedBeans, updated);

			getUpdatedRowFieldAppTypeTabValList(managedBeans);
		} else {
			updated = false;
			/*
			 * New created
			 */
			this.docGroupId = BigInteger.valueOf(managedBeans.getSelectedDocument().getDocumentId());
			this.docId = BigInteger.valueOf(managedBeans.getSelectedDocument().getDocumentId());

			/* 1 */
			detailObj(AuditPMSupplierMgtConfigConst.PM_AUD_SD_DISPLAY_PRIORITY_FIELDLABELID, null, managedBeans.getResDTO()
					.getDocument().getDisplaySeq(), AuditPMSupplierMgtConfigConst.PM_AUD_ACTION_TYPE_INS);
			/* 2 */
			detailObj(AuditPMSupplierMgtConfigConst.PM_AUD_SD_DOCUMENTNAME_FIELDLABELID, null, managedBeans.getResDTO()
					.getDocument().getDocName(), AuditPMSupplierMgtConfigConst.PM_AUD_ACTION_TYPE_INS);
			/* 3 */
			detailObj(AuditPMSupplierMgtConfigConst.PM_AUD_SD_ZONE_FIELDLABELID, null, managedBeans.getAuditPmZone().getZoneName(), 
					AuditPMSupplierMgtConfigConst.PM_AUD_ACTION_TYPE_INS);
			/* 4 */
			detailObj(AuditPMSupplierMgtConfigConst.PM_AUD_SD_STATUS_FIELDLABELID, null, managedBeans.getResDTO().getDocument()
					.getRecordStatusFlg() == 1 ? AuditPMSupplierMgtConfigConst.PM_AUD_ACTIVE
					: AuditPMSupplierMgtConfigConst.PM_AUD_INACTIVE, AuditPMSupplierMgtConfigConst.PM_AUD_ACTION_TYPE_INS);

			/* Create new row list in new mode */
			handleCheckboxUpdateAdded(managedBeans, updated);

			/* Create new row list in update/create mode */
			updateCreateTablist(managedBeans, updated);
		}

		clearmMagedBeans(managedBeans);

		auditPMReqDTO.setObjList(objList);

		return auditPMReqDTO;
	}

	private void handleCheckboxUpdateAdded(SupportDocumentManagedBean managedBeans, boolean updated) {
		updatedAddAppTypeChkbox(managedBeans, updated); // 5

		updatedAddCompTypeChkbox(managedBeans, updated); // 6
		
		updatedAddCategoryChkbox(managedBeans, updated); // 7

		updatedAddSupplierTypeChkbox(managedBeans, updated); // 8

		updatedAddCertBodyChkbox(managedBeans, updated); // 9

		updatedMandatoryChkbox(managedBeans, updated); // 10

		updatedLicenseChkbox(managedBeans, updated); // 11

		updatedHardCopyChkbox(managedBeans, updated); // 12

		updatedGSTChkbox(managedBeans, updated); // 13
	}

	/* 5 */
	private void updatedAddAppTypeChkbox(SupportDocumentManagedBean managedBeans, boolean updated) {
		if (null != managedBeans.getSelectedDocumentOld()
				&& managedBeans.getSelectedDocument().isByAppType() != managedBeans.getSelectedDocumentOld().isByAppType()
				|| !updated) {
			this.docGroupId = BigInteger.valueOf(managedBeans.getSelectedDocument().getDocumentId());
			this.docId = BigInteger.valueOf(managedBeans.getSelectedDocument().getDocumentId());

			detailObj(
					AuditPMSupplierMgtConfigConst.PM_AUD_SD_APPTYPECHKBOX_FIELDLABELID,
					updated ? oldValue(managedBeans.getSelectedDocument().isByAppType(), managedBeans.getSelectedDocumentOld()
							.isByAppType(), updated, "isByAppType") : null, yesNo(managedBeans.getSelectedDocument()
							.isByAppType()), actionType(updated));

			if (null != managedBeans.getAppTypeRestrictionsOld()
					&& newValue(managedBeans.getSelectedDocument().isByAppType()) != null
					&& newValue(managedBeans.getSelectedDocument().isByAppType()).equalsIgnoreCase(
							AuditPMSupplierMgtConfigConst.PM_AUD_INACTIVE)) {
				managedBeans.setDeleteAppTypeRestrictionsAuditList(new ArrayList<PmDocumentRestr>());
				managedBeans.getDeleteAppTypeRestrictionsAuditList().addAll(managedBeans.getAppTypeRestrictionsOld());
			}
		}
	}

	/* 6 */
	private void updatedAddCompTypeChkbox(SupportDocumentManagedBean managedBeans, boolean updated) {
		// /* Restriction Document - detailObjCompTypeChkbox */
		if (null != managedBeans.getSelectedDocumentOld()
				&& managedBeans.getSelectedDocument().isByCompType() != managedBeans.getSelectedDocumentOld().isByCompType()
				|| !updated) {
			this.docGroupId = BigInteger.valueOf(managedBeans.getSelectedDocument().getDocumentId());
			this.docId = BigInteger.valueOf(managedBeans.getSelectedDocument().getDocumentId());

			detailObj(
					AuditPMSupplierMgtConfigConst.PM_AUD_SD_COMPTYPECHKBOX_FIELDLABELID,
					updated ? oldValue(managedBeans.getSelectedDocument().isByCompType(), managedBeans.getSelectedDocumentOld()
							.isByCompType(), updated, "isByCompType") : null, yesNo(managedBeans.getSelectedDocument()
							.isByCompType()), actionType(updated));

			if (null != managedBeans.getComTypeRestrictionsOld()
					&& newValue(managedBeans.getSelectedDocument().isByCompType()) != null
					&& newValue(managedBeans.getSelectedDocument().isByCompType()).equalsIgnoreCase(
							AuditPMSupplierMgtConfigConst.PM_AUD_INACTIVE)) {
				managedBeans.setDeleteComTypeRestrictionsAudit(new ArrayList<PmDocumentRestr>());
				managedBeans.getDeleteComTypeRestrictionsAudit().addAll(managedBeans.getComTypeRestrictionsOld());

				if (managedBeans.getNewCompanyCodeTypeAuditList() != null) {
					managedBeans.getNewCompanyCodeTypeAuditList().add(new PmDocumentRestr());
					managedBeans.getNewCompanyCodeTypeAuditList().clear();
				}
			}
		}
	}

	/* 7 */
	private void updatedAddCategoryChkbox(SupportDocumentManagedBean managedBeans, boolean updated) {
		// /* Restriction Document - detailObjCategoryChkbox */
		if (null != managedBeans.getSelectedDocumentOld()
				&& managedBeans.getSelectedDocument().isByCategory() != managedBeans.getSelectedDocumentOld().isByCategory()
				|| !updated) {
			this.docGroupId = BigInteger.valueOf(managedBeans.getSelectedDocument().getDocumentId());
			this.docId = BigInteger.valueOf(managedBeans.getSelectedDocument().getDocumentId());
			detailObj(
					AuditPMSupplierMgtConfigConst.PM_AUD_SD_CATEGORYCHKBOX_FIELDLABELID,
					updated ? oldValue(managedBeans.getSelectedDocument().isByCategory(), managedBeans.getSelectedDocumentOld()
							.isByCategory(), updated, "isByCategory") : null, yesNo(managedBeans.getSelectedDocument()
							.isByCategory()), actionType(updated));

			if (null != managedBeans.getCategoryRestrictionsOld()
					&& managedBeans.getCategoryRestrictionsOld().iterator().next().getPmCategoryL3() != null
					&& newValue(managedBeans.getSelectedDocument().isByCategory()) != null
					&& newValue(managedBeans.getSelectedDocument().isByCategory()).equalsIgnoreCase(
							AuditPMSupplierMgtConfigConst.PM_AUD_INACTIVE)) {
				managedBeans.setDeleteCategoryRestrictionsAudit(new ArrayList<PmDocumentRestr>());
				managedBeans.getDeleteCategoryRestrictionsAudit().addAll(managedBeans.getCategoryRestrictionsOld());

				if (managedBeans.getNewCategoryL3AuditList() != null) {
					managedBeans.getNewCategoryL3AuditList().add(new PmDocumentRestr());
					managedBeans.getNewCategoryL3AuditList().clear();
				}
			}
		}
	}

	/* 8 */
	private void updatedAddSupplierTypeChkbox(SupportDocumentManagedBean managedBeans, boolean updated) {
		// /* Restriction Document - detailObjSupplierTypeChkbox */
		if (null != managedBeans.getSelectedDocumentOld()
				&& managedBeans.getSelectedDocument().isBySupplierType() != managedBeans.getSelectedDocumentOld()
						.isBySupplierType() || !updated) {
			this.docGroupId = BigInteger.valueOf(managedBeans.getSelectedDocument().getDocumentId());
			this.docId = BigInteger.valueOf(managedBeans.getSelectedDocument().getDocumentId());
			detailObj(
					AuditPMSupplierMgtConfigConst.PM_AUD_SD_SUPPLIERTYPECHKBOX_FIELDLABELID,
					updated ? oldValue(managedBeans.getSelectedDocument().isBySupplierType(), managedBeans
							.getSelectedDocumentOld().isBySupplierType(), updated, "isBySupplierType") : null, yesNo(managedBeans
							.getSelectedDocument().isBySupplierType()), actionType(updated));

			if (null != managedBeans.getSuppTypeRestrictionsOld()
					&& newValue(managedBeans.getSelectedDocument().isBySupplierType()) != null
					&& newValue(managedBeans.getSelectedDocument().isBySupplierType()).equalsIgnoreCase(
							AuditPMSupplierMgtConfigConst.PM_AUD_INACTIVE)) {
				managedBeans.setDeleteSuppTypeRestrictionsAudit(new ArrayList<PmDocumentRestr>());
				managedBeans.getDeleteSuppTypeRestrictionsAudit().addAll(managedBeans.getSuppTypeRestrictionsOld());
			}
		}
	}

	/* 9 */
	private void updatedAddCertBodyChkbox(SupportDocumentManagedBean managedBeans, boolean updated) {
		if (null != managedBeans.getSelectedDocumentOld()
				&& managedBeans.getSelectedDocument().isByCertBody() != managedBeans.getSelectedDocumentOld().isByCertBody()
				|| !updated) {
			this.docGroupId = BigInteger.valueOf(managedBeans.getSelectedDocument().getDocumentId());
			this.docId = BigInteger.valueOf(managedBeans.getSelectedDocument().getDocumentId());
			detailObj(
					AuditPMSupplierMgtConfigConst.PM_AUD_SD_CERTBODYCHKBOX_FIELDLABELID,
					updated ? oldValue(managedBeans.getSelectedDocument().isByCertBody(), managedBeans.getSelectedDocumentOld()
							.isByCertBody(), updated, "isByCertBody") : null, yesNo(managedBeans.getSelectedDocument()
							.isByCertBody()), actionType(updated));

			if (null != managedBeans.getCertBodyRestrictionsOld()
					&& newValue(managedBeans.getSelectedDocument().isByCertBody()) != null
					&& newValue(managedBeans.getSelectedDocument().isByCertBody()).equalsIgnoreCase(
							AuditPMSupplierMgtConfigConst.PM_AUD_INACTIVE)) {
				managedBeans.setDeleteCertBodyRestrictionsAudit(new ArrayList<PmDocumentRestr>());
				managedBeans.getDeleteCertBodyRestrictionsAudit().addAll(managedBeans.getCertBodyRestrictionsOld());
			}
		}
	}

	/* 10 */
	private void updatedHardCopyChkbox(SupportDocumentManagedBean managedBeans, boolean updated) {
		/* Document Type - detailObjHardCopyChkbox */
		if (null != managedBeans.getSelectedDocumentOld()
				&& managedBeans.getSelectedDocument().isHardCopyReq() != managedBeans.getSelectedDocumentOld().isHardCopyReq()
				|| !updated) {
			this.docGroupId = BigInteger.valueOf(managedBeans.getSelectedDocument().getDocumentId());
			this.docId = BigInteger.valueOf(managedBeans.getSelectedDocument().getDocumentId());
			detailObj(
					AuditPMSupplierMgtConfigConst.PM_AUD_SD_HARDCOPYCHKBOX_FIELDLABELID,
					updated ? oldValue(managedBeans.getSelectedDocument().isHardCopyReq(), managedBeans.getSelectedDocumentOld()
							.isHardCopyReq(), updated, "isHardCopyReq") : null, yesNo(managedBeans.getSelectedDocument()
							.isHardCopyReq()), actionType(updated));
		}
	}

	/* 11 */
	private void updatedLicenseChkbox(SupportDocumentManagedBean managedBeans, boolean updated) {
		/* Document Type - detailObjLicenseChkbox */
		if (null != managedBeans.getSelectedDocumentOld()
				&& managedBeans.getSelectedDocument().isLicenseReq() != managedBeans.getSelectedDocumentOld().isLicenseReq()
				|| !updated) {
			this.docGroupId = BigInteger.valueOf(managedBeans.getSelectedDocument().getDocumentId());
			this.docId = BigInteger.valueOf(managedBeans.getSelectedDocument().getDocumentId());
			detailObj(
					AuditPMSupplierMgtConfigConst.PM_AUD_SD_LICENSECHKBOX_FIELDLABELID,
					updated ? oldValue(managedBeans.getSelectedDocument().isLicenseReq(), managedBeans.getSelectedDocumentOld()
							.isLicenseReq(), updated, "isLicenseReq") : null, yesNo(managedBeans.getSelectedDocument()
							.isLicenseReq()), actionType(updated));
		}
	}

	/* 12 */
	private void updatedMandatoryChkbox(SupportDocumentManagedBean managedBeans, boolean updated) {
		if (null != managedBeans.getSelectedDocumentOld()
				&& managedBeans.getSelectedDocument().isMandatory() != managedBeans.getSelectedDocumentOld().isMandatory()
				|| !updated) {
			this.docGroupId = BigInteger.valueOf(managedBeans.getSelectedDocument().getDocumentId());
			this.docId = BigInteger.valueOf(managedBeans.getSelectedDocument().getDocumentId());
			detailObj(
					AuditPMSupplierMgtConfigConst.PM_AUD_SD_MANDATORYCHKBOX_FIELDLABELID,
					updated ? oldValue(managedBeans.getSelectedDocument().isMandatory(), managedBeans.getSelectedDocumentOld()
							.isMandatory(), updated, "isMandatory") : null, yesNo(managedBeans.getSelectedDocument()
							.isMandatory()), actionType(updated));
		}
	}

	/* 13 */
	private void updatedGSTChkbox(SupportDocumentManagedBean managedBeans, boolean updated) {
		/* Document Type - detailObjGSTChkbox */
		if (null != managedBeans.getSelectedDocumentOld()
				&& managedBeans.getSelectedDocument().isGst() != managedBeans.getSelectedDocumentOld().isGst() || !updated) {
			this.docGroupId = BigInteger.valueOf(managedBeans.getSelectedDocument().getDocumentId());
			this.docId = BigInteger.valueOf(managedBeans.getSelectedDocument().getDocumentId());
			detailObj(
					AuditPMSupplierMgtConfigConst.PM_AUD_SD_GSTCHKBOX_FIELDLABELID,
					updated ? oldValue(managedBeans.getSelectedDocument().isGst(), managedBeans.getSelectedDocumentOld().isGst(),
							updated, "isGst") : null, yesNo(managedBeans.getSelectedDocument().isGst()), actionType(updated));
		}
	}

	private void updateCreateTablist(SupportDocumentManagedBean managedBeans, boolean updated) {
		/* 14/15/16/17/18/19/20/21 */
		updatedCreatedAppTypeCodeTabList(managedBeans, updated);

		/* 22 */
		if (null != managedBeans.getNewCompanyCodeTypeAuditList() && !managedBeans.getNewCompanyCodeTypeAuditList().isEmpty())
			updatedCreatedCompTypeCodeTabList(managedBeans, updated);

		/* 23 */
		updatedCreatedSuppTypeCodeTabList(managedBeans, updated);

		/* 24/25/26/27 */
		if (null != managedBeans.getNewCategoryL3AuditList() && !managedBeans.getNewCategoryL3AuditList().isEmpty())
			updatedCreatedCatTypeCodeTabList(managedBeans, updated);

		/* 28 */
		updatedCreatedCertBodyTypeCodeTabList(managedBeans, updated);
	}

	/* 14/15/16/17/18/19/20/21 */
	private void updatedCreatedAppTypeCodeTabList(SupportDocumentManagedBean managedBeans, boolean updated) {
		AuditPMSupportingDocumentNewListBackingBean newListMapBean = new AuditPMSupportingDocumentNewListBackingBean();
		if (null != managedBeans.getMandatoryUpdatedPmDocumentRestrList()
				&& !managedBeans.getMandatoryUpdatedPmDocumentRestrList().isEmpty()) {
			for (PmDocumentRestr pmDocumentRestr : managedBeans.getMandatoryUpdatedPmDocumentRestrList()) {
				for (PmDocumentRestr pmDocumentRestrApp : managedBeans.getAppTypeRestrictions()) {
					if (pmDocumentRestr.isBrandNew()
							&& managedBeans.getSelectedDocument().isByAppType()
							&& pmDocumentRestr.getAppTypeCode() != null
							&& pmDocumentRestrApp.getAppTypeCode().getParameterId() == pmDocumentRestr.getAppTypeCode()
									.getParameterId()) {
						pmDocumentRestr.setDocumentRestrId(pmDocumentRestrApp.getDocumentRestrId());
						appTypeRestrictionsNew.add(pmDocumentRestrApp);
					}
				}
			}
		}
		managedBeans.setAppTypeRestrictionsNew(appTypeRestrictionsNew);

		if (null != managedBeans.getAppTypeRestrictionsNew() && !managedBeans.getAppTypeRestrictionsNew().isEmpty()) {
			objList.addAll(newListMapBean.updatedNewCreateRowAppTypeTabList(managedBeans, roleCode, updated));
		}
	}

	/* 22 */
	private void updatedCreatedCompTypeCodeTabList(SupportDocumentManagedBean managedBeans, boolean updated) {
		AuditPMSupportingDocumentNewListBackingBean newListMapBean = new AuditPMSupportingDocumentNewListBackingBean();
		if (null != managedBeans.getNewCompanyCodeTypeAuditList() && !managedBeans.getNewCompanyCodeTypeAuditList().isEmpty()) {
			log.info("managedBeans.getNewCompanyCodeTypeAuditList() ####__***::: "
					+ managedBeans.getNewCompanyCodeTypeAuditList().size());
			for (PmDocumentRestr restNew : managedBeans.getNewCompanyCodeTypeAuditList()) {
				for (PmDocumentRestr rest : managedBeans.getComTypeRestrictions()) {
					if (rest.getCompanyTypeCode().getParameterId() == restNew.getCompanyTypeCode().getParameterId()) {
						restNew.setDocumentRestrId(rest.getDocumentRestrId());
					} else {
						restNew.setDocumentRestrId(rest.getDocumentRestrId());
					}
				}
			}

			objList.addAll(newListMapBean.updatedNewCreateRowComTypeTabList(managedBeans, roleCode, updated));
		}
	}

	/* 23 */
	private void updatedCreatedSuppTypeCodeTabList(SupportDocumentManagedBean managedBeans, boolean updated) {
		AuditPMSupportingDocumentNewListBackingBean newListMapBean = new AuditPMSupportingDocumentNewListBackingBean();
		if (null != managedBeans.getMandatoryUpdatedPmDocumentRestrList()
				&& !managedBeans.getMandatoryUpdatedPmDocumentRestrList().isEmpty()) {
			for (PmDocumentRestr pmDocumentRestr : managedBeans.getMandatoryUpdatedPmDocumentRestrList()) {
				for (PmDocumentRestr pmDocumentRestrSupp : managedBeans.getSuppTypeRestrictions()) {
					if (pmDocumentRestr.isBrandNew()
							&& managedBeans.getSelectedDocument().isBySupplierType()
							&& pmDocumentRestr.getSupplierTypeCode() != null
							&& pmDocumentRestrSupp.getSupplierTypeCode().getParameterId() == pmDocumentRestr
									.getSupplierTypeCode().getParameterId()) {
						pmDocumentRestr.setDocumentRestrId(pmDocumentRestrSupp.getDocumentRestrId());
						suppTypeRestrictionsNew.add(pmDocumentRestrSupp);
					}
				}
			}
		}
		managedBeans.setSuppTypeRestrictionsNew(suppTypeRestrictionsNew);
		if (null != managedBeans.getSuppTypeRestrictionsNew() && !managedBeans.getSuppTypeRestrictionsNew().isEmpty()) {
			objList.addAll(newListMapBean.updatedNewCreateRowSuppTypeTabList(managedBeans, roleCode, updated));
		}
	}

	/* 24/25/26/27 */
	private void updatedCreatedCatTypeCodeTabList(SupportDocumentManagedBean managedBeans, boolean updated) {
		AuditPMSupportingDocumentNewListBackingBean newListMapBean = new AuditPMSupportingDocumentNewListBackingBean();
		if (null != managedBeans.getNewCategoryL3AuditList() && !managedBeans.getNewCategoryL3AuditList().isEmpty()) {
			for (PmDocumentRestr restNew : managedBeans.getNewCategoryL3AuditList()) {
				for (PmDocumentRestr rest : managedBeans.getCategoryRestrictions()) {
					if (rest.getPmCategoryL3().getCategoryL3Id() == restNew.getPmCategoryL3().getCategoryL3Id()) {
						restNew.setDocumentRestrId(rest.getDocumentRestrId());
					} else {
						restNew.setDocumentRestrId(rest.getDocumentRestrId());
					}
				}
			}
			objList.addAll(newListMapBean.updatedNewCreateRowCategoryTypeTabList(managedBeans, roleCode, updated));
		}
	}

	/* 28 */
	private void updatedCreatedCertBodyTypeCodeTabList(SupportDocumentManagedBean managedBeans, boolean updated) {
		AuditPMSupportingDocumentNewListBackingBean newListMapBean = new AuditPMSupportingDocumentNewListBackingBean();
		if (null != managedBeans.getMandatoryUpdatedPmDocumentRestrList()
				&& !managedBeans.getMandatoryUpdatedPmDocumentRestrList().isEmpty()) {
			for (PmDocumentRestr pmDocumentRestr : managedBeans.getMandatoryUpdatedPmDocumentRestrList()) {
				for (PmDocumentRestr pmDocumentRestrCertBody : managedBeans.getCertBodyRestrictions()) {
					if (pmDocumentRestr.isBrandNew()
							&& pmDocumentRestr.getPmCertBody() != null
							&& pmDocumentRestr.getPmCertBody().getCertBodyId() == pmDocumentRestrCertBody.getPmCertBody()
									.getCertBodyId()) {
						pmDocumentRestr.setDocumentRestrId(pmDocumentRestrCertBody.getDocumentRestrId());
						certBodyRestrictionsNew.add(pmDocumentRestr);

					}
				}
			}
		}

		managedBeans.setCertBodyRestrictionsNew(certBodyRestrictionsNew);
		if (null != managedBeans.getCertBodyRestrictionsNew() && !managedBeans.getCertBodyRestrictionsNew().isEmpty()) {
			objList.addAll(newListMapBean.updatedNewCreateRowCertBodyTypeTabList(managedBeans, roleCode, updated));
		}
	}

	private void getOldValList(SupportDocumentManagedBean managedBeans) {
		SupportDocumentBackingBean bean = new SupportDocumentBackingBean();
		for (PmDocumentRestr pmDocumentRestr : managedBeans.getSelectedDocumentOld().getPmDocumentRestrs()) {
			if (DbValueConst.RECORD_STATUS_ACTIVE == pmDocumentRestr.getRecordStatusFlg()) {
				if (pmDocumentRestr.getAppTypeCode() != null) {
					handleGetOldValueList(pmDocumentRestr, managedBeans);
				}
				if (pmDocumentRestr.getCompanyTypeCode() != null) {
					handleGetOldValueList(pmDocumentRestr, managedBeans);
				}
				if (pmDocumentRestr.getSupplierTypeCode() != null) {
					handleGetOldValueList(pmDocumentRestr, managedBeans);
				}
				if (managedBeans.getSelectedDocumentOld().isByCategory() && pmDocumentRestr.getPmCategoryL3() != null) {
					pmDocumentRestr.setPmCategoryL3(bean.retrieveCategoryL3DtlById(pmDocumentRestr.getPmCategoryL3()
							.getCategoryL3Id()));
					categoryRestrictionsOld.add(pmDocumentRestr);
					managedBeans.setCategoryRestrictionsOld(categoryRestrictionsOld);
				}
				if (pmDocumentRestr.getPmCertBody() != null) {
					handleGetOldValueList(pmDocumentRestr, managedBeans);
				}
			}
		}
	}

	private void handleGetOldValueList(PmDocumentRestr pmDocumentRestr, SupportDocumentManagedBean managedBeans) {
		if (managedBeans.getSelectedDocumentOld().isByAppType()) {
			appTypeRestrictionsOld.add(pmDocumentRestr);
			managedBeans.setAppTypeRestrictionsOld(appTypeRestrictionsOld);
		}
		if (managedBeans.getSelectedDocumentOld().isByCompType()) {
			comTypeRestrictionsOld.add(pmDocumentRestr);
			managedBeans.setComTypeRestrictionsOld(comTypeRestrictionsOld);
		}
		if (managedBeans.getSelectedDocumentOld().isBySupplierType()) {
			suppTypeRestrictionsOld.add(pmDocumentRestr);
			managedBeans.setSuppTypeRestrictionsOld(suppTypeRestrictionsOld);
		}
		if (managedBeans.getSelectedDocumentOld().isByCertBody()) {
			certBodyRestrictionsOld.add(pmDocumentRestr);
			managedBeans.setCertBodyRestrictionsOld(certBodyRestrictionsOld);
		}
	}

	/* 15-21 */
	private void getUpdatedRowFieldAppTypeTabValList(SupportDocumentManagedBean managedBeans) {
		if (null != managedBeans.getSelectedDocument().getPmDocumentRestrs()) {
			for (PmDocumentRestr pmDocumentRestr : managedBeans.getAppTypeRestrictions()) {
				if (!pmDocumentRestr.isBrandNew() && managedBeans.getSelectedDocument().isByAppType()
						&& pmDocumentRestr.getAppTypeCode() != null && !pmDocumentRestr.isDeleted()) {
					appTypeRestrictionsUpdated.add(pmDocumentRestr);
					managedBeans.setAppTypeRestrictionsUpdated(appTypeRestrictionsUpdated);
				}
			}
			if (null != managedBeans.getAppTypeRestrictionsUpdated() && !managedBeans.getAppTypeRestrictionsUpdated().isEmpty()
					&& managedBeans.getAppTypeRestrictionsOld() != null) {
				updatedRowFieldAppTypeTabValList(managedBeans);
			}
		}
	}

	/* update field value in the row list for AppType Tab #15-21 */
	private void updatedRowFieldAppTypeTabValList(SupportDocumentManagedBean managedBeans) {
		for (PmDocumentRestr pmDocumentRestr : managedBeans.getAppTypeRestrictions()) {
			for (PmDocumentRestr pmDocumentRestrOld : managedBeans.getAppTypeRestrictionsOld()) {
				if (pmDocumentRestr.getDocumentRestrId().equals(pmDocumentRestrOld.getDocumentRestrId())) {
					handleUpdatedRowFieldAppTypeTabValList(pmDocumentRestrOld, pmDocumentRestr, managedBeans);
				}
			}
		}
	}

	private void handleUpdatedRowFieldAppTypeTabValList(PmDocumentRestr pmDocumentRestrOld, PmDocumentRestr pmDocumentRestr,
			SupportDocumentManagedBean managedBeans) {
		/*
		 * Restriction Document - AppTypeList - Section : TextList -
		 * isCompanyInfoChanged #15
		 */
		if (!Boolean.valueOf(pmDocumentRestr.isCompanyInfoChanged()).equals(pmDocumentRestrOld.isCompanyInfoChanged())) {
			this.docGroupId = BigInteger.valueOf(managedBeans.getSelectedDocument().getDocumentId());
			this.docId = BigInteger.valueOf(pmDocumentRestr.getDocumentRestrId());
			detailObj(AuditPMSupplierMgtConfigConst.PM_AUD_SD_COMPANYINFO_FIELDLABELID,
					yesNo(pmDocumentRestrOld.isCompanyInfoChanged()), yesNo(pmDocumentRestr.isCompanyInfoChanged()),
					AuditPMSupplierMgtConfigConst.PM_AUD_ACTION_TYPE_UPD);
		}
		/*
		 * Restriction Document - AppTypeList - Section : TextList -
		 * isAsscProjChanged() #16
		 */
		if (!Boolean.valueOf(pmDocumentRestr.isAsscProjChanged()).equals(pmDocumentRestrOld.isAsscProjChanged())) {
			this.docGroupId = BigInteger.valueOf(managedBeans.getSelectedDocument().getDocumentId());
			this.docId = BigInteger.valueOf(pmDocumentRestr.getDocumentRestrId());
			detailObj(AuditPMSupplierMgtConfigConst.PM_AUD_SD_ASSCPROJECT_FIELDLABELID,
					yesNo(pmDocumentRestrOld.isAsscProjChanged()), yesNo(pmDocumentRestr.isAsscProjChanged()),
					AuditPMSupplierMgtConfigConst.PM_AUD_ACTION_TYPE_UPD);
		}

		/*
		 * Restriction Document - AppTypeList - Section : TextList -
		 * isPersonnelBranchChanged() #17
		 */
		if (pmDocumentRestrOld.isPersonnelBranchChanged() != pmDocumentRestr.isPersonnelBranchChanged()) {
			this.docGroupId = BigInteger.valueOf(managedBeans.getSelectedDocument().getDocumentId());
			this.docId = BigInteger.valueOf(pmDocumentRestr.getDocumentRestrId());
			detailObj(AuditPMSupplierMgtConfigConst.PM_AUD_SD_PERSONALBRANCH_FIELDLABELID,
					yesNo(pmDocumentRestrOld.isPersonnelBranchChanged()), yesNo(pmDocumentRestr.isPersonnelBranchChanged()),
					AuditPMSupplierMgtConfigConst.PM_AUD_ACTION_TYPE_UPD);
		}
		/*
		 * Restriction Document - AppTypeList - Section : TextList -
		 * isCapitalEquityChanged() #18
		 */
		if (pmDocumentRestrOld.isCapitalEquityChanged() != pmDocumentRestr.isCapitalEquityChanged()) {
			this.docGroupId = BigInteger.valueOf(managedBeans.getSelectedDocument().getDocumentId());
			this.docId = BigInteger.valueOf(pmDocumentRestr.getDocumentRestrId());
			detailObj(AuditPMSupplierMgtConfigConst.PM_AUD_SD_CAPITALEQUITY_FIELDLABELID,
					yesNo(pmDocumentRestrOld.isCapitalEquityChanged()), yesNo(pmDocumentRestr.isCapitalEquityChanged()),
					AuditPMSupplierMgtConfigConst.PM_AUD_ACTION_TYPE_UPD);
		}
		/*
		 * Restriction Document - AppTypeList - Section : TextList -
		 * isBankChanged() #19
		 */
		if (pmDocumentRestrOld.isBankChanged() != pmDocumentRestr.isBankChanged()) {
			this.docGroupId = BigInteger.valueOf(managedBeans.getSelectedDocument().getDocumentId());
			this.docId = BigInteger.valueOf(pmDocumentRestr.getDocumentRestrId());
			detailObj(AuditPMSupplierMgtConfigConst.PM_AUD_SD_BANKINFO_FIELDLABELID, yesNo(pmDocumentRestrOld.isBankChanged()),
					yesNo(pmDocumentRestr.isBankChanged()), AuditPMSupplierMgtConfigConst.PM_AUD_ACTION_TYPE_UPD);
		}

		// /* Facility Type : Home/Business Office (Y/N) #20-21*/
		updatedRowFieldAppTypeTabFacilityType(pmDocumentRestrOld, pmDocumentRestr, managedBeans);

	}

	/* Facility Type : Home/Business Office (Y/N) #20-21 */
	private void updatedRowFieldAppTypeTabFacilityType(PmDocumentRestr pmDocumentRestrOld, PmDocumentRestr pmDocumentRestr,
			SupportDocumentManagedBean managedBeans) {
		/* Facility Type : Home Office (Y/N) #20 */
		if (pmDocumentRestrOld.isHomeOffice() != pmDocumentRestr.isHomeOffice()
				&& pmDocumentRestr.getDocumentRestrId().equals(pmDocumentRestrOld.getDocumentRestrId())) {
			this.docGroupId = BigInteger.valueOf(managedBeans.getSelectedDocument().getDocumentId());
			this.docId = BigInteger.valueOf(pmDocumentRestr.getDocumentRestrId());
			detailObj(AuditPMSupplierMgtConfigConst.PM_AUD_SD_FACILITYHOME_FIELDLABELID,
					yesNo(pmDocumentRestrOld.isHomeOffice()), yesNo(pmDocumentRestr.isHomeOffice()),
					AuditPMSupplierMgtConfigConst.PM_AUD_ACTION_TYPE_UPD);
		}
		/* Facility Type : Business Office (Y/N) #21 */
		if (pmDocumentRestrOld.isBusinessOffice() != pmDocumentRestr.isBusinessOffice()
				&& pmDocumentRestr.getDocumentRestrId().equals(pmDocumentRestrOld.getDocumentRestrId())) {
			this.docGroupId = BigInteger.valueOf(managedBeans.getSelectedDocument().getDocumentId());
			this.docId = BigInteger.valueOf(pmDocumentRestr.getDocumentRestrId());
			detailObj(AuditPMSupplierMgtConfigConst.PM_AUD_SD_FACILITYOFFICE_FIELDLABELID,
					yesNo(pmDocumentRestrOld.isBusinessOffice()), yesNo(pmDocumentRestr.isBusinessOffice()),
					AuditPMSupplierMgtConfigConst.PM_AUD_ACTION_TYPE_UPD);
		}
	}

	private void deleted(SupportDocumentManagedBean managedBeans) {
		AuditPMSupportingDocumentDeletedBackingBean deletedMapbean = new AuditPMSupportingDocumentDeletedBackingBean();
		/* 14-21 */
		if (null != managedBeans.getDeleteAppTypeRestrictionsAuditList()
				&& !managedBeans.getDeleteAppTypeRestrictionsAuditList().isEmpty()) {
			objList.addAll(deletedMapbean.deleteAppType(managedBeans, roleCode));
		}
		/* 22 */
		if (null != managedBeans.getDeleteComTypeRestrictionsAudit()
				&& !managedBeans.getDeleteComTypeRestrictionsAudit().isEmpty()) {
			objList.addAll(deletedMapbean.deleteComType(managedBeans, roleCode));
		}
		/* 23 */
		if (null != managedBeans.getDeleteSuppTypeRestrictionsAudit()
				&& !managedBeans.getDeleteSuppTypeRestrictionsAudit().isEmpty()) {
			objList.addAll(deletedMapbean.deleteSuppType(managedBeans, roleCode));
		}
		/* 24-27 */
		if (null != managedBeans.getDeleteCategoryRestrictionsAudit()
				&& !managedBeans.getDeleteCategoryRestrictionsAudit().isEmpty()) {
			objList.addAll(deletedMapbean.deleteCategoryType(managedBeans, roleCode));
		}
		/* 28 */
		if (null != managedBeans.getDeleteCertBodyRestrictionsAudit()) {
			objList.addAll(deletedMapbean.deleteCertBodyType(managedBeans, roleCode));
		}
		managedBeans.setDeleteAppTypeRestrictionsAuditList(null);
		managedBeans.setDeleteComTypeRestrictionsAudit(null);
		managedBeans.setDeleteSuppTypeRestrictionsAudit(null);
		managedBeans.setDeleteCategoryRestrictionsAudit(null);
		managedBeans.setDeleteCertBodyRestrictionsAudit(null);
	}

	private ArrayList<AuditPMSPDTO> detailObj(BigInteger fieldLabelId, String oldValue, String newValue, String actionType) {
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
		return objList;
	}

	static {
		Set<String> checkboxes = new HashSet<>();
		checkboxes.add("isByAppType");
		checkboxes.add("isByCompType");
		checkboxes.add("isBySupplierType");
		checkboxes.add("isByCategory");
		checkboxes.add("isByCertBody");
		checkboxes.add("isMandatory");
		checkboxes.add("isLicenseReq");
		checkboxes.add("isHardCopyReq");
		checkboxes.add("isGst");
		HANDLED_CHECKBOXES = Collections.unmodifiableSet(checkboxes);
	}

	private boolean isHandledChkbox(String chkboxName) {
		return HANDLED_CHECKBOXES.contains(chkboxName);
	}

	private String oldValue(boolean selectedDocument, boolean selectedDocumentOld, boolean updated, String chkboxName) {
		if (!updated)
			return null;

		if (isHandledChkbox(chkboxName)) {
			return handleFieldOldValue(selectedDocument, selectedDocumentOld);
		}

		// Handle unexpected or unhandled cases explicitly, if needed
		return null;
	}

	private String handleFieldOldValue(boolean selectedDocument, boolean selectedDocumentOld) {
		if (!Boolean.valueOf(selectedDocument).equals(selectedDocumentOld)) {
			return oldVal(selectedDocumentOld);
		}
		return null;
	}

	private String oldVal(boolean oldValueUpdated) {
		return oldValueUpdated ? AuditPMSupplierMgtConfigConst.PM_AUD_YES : AuditPMSupplierMgtConfigConst.PM_AUD_NO;
	}

	private String newValue(boolean checkboxUpdated) {
		return checkboxUpdated ? AuditPMSupplierMgtConfigConst.PM_AUD_ACTIVE : AuditPMSupplierMgtConfigConst.PM_AUD_INACTIVE;
	}

	private String actionType(boolean actionTypeUpdated) {
		return actionTypeUpdated ? AuditPMSupplierMgtConfigConst.PM_AUD_ACTION_TYPE_UPD
				: AuditPMSupplierMgtConfigConst.PM_AUD_ACTION_TYPE_INS;
	}

	private String yesNo(boolean yesNo) {
		return yesNo ? AuditPMSupplierMgtConfigConst.PM_AUD_YES : AuditPMSupplierMgtConfigConst.PM_AUD_NO;
	}

	private void clearmMagedBeans(SupportDocumentManagedBean managedBeans) {
		managedBeans.setSelectedDocumentOld(null);
		managedBeans.getResDTO().setUpdatedPmDocumentRestrList(null);
		managedBeans.setMandatoryUpdatedPmDocumentRestrList(null);
		managedBeans.setComTypeRestrictionsNew(null);
		managedBeans.setSuppTypeRestrictionsNew(null);
		managedBeans.setCertBodyRestrictionsNew(null);
		managedBeans.setAppTypeRestrictionsNew(null);
		managedBeans.setCategoryRestrictionsNew(null);
		managedBeans.setNewCategoryL3AuditList(null);
		appTypeRestrictionsNew.clear();
		certBodyRestrictionsNew.clear();
		categoryRestrictionsNew.clear();
		suppTypeRestrictionsNew.clear();
		appTypeRestrictionsNew.clear();
	}
}