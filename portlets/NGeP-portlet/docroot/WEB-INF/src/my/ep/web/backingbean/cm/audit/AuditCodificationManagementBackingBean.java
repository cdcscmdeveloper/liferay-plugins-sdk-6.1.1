package my.ep.web.backingbean.cm.audit;

import java.util.ArrayList;
import java.util.List;

import my.ep.biz.bean.search.SearchBean;
import my.ep.biz.bean.search.SearchFilterBean;
import my.ep.biz.service.cm.codification.ICodificationSO;
import my.ep.constant.ModuleCodeConst;
import my.ep.constant.cm.AuditCMConst;
import my.ep.dto.cm.audit.AuditCodificationManagementDTO;
import my.ep.dto.cm.audit.AuditCodificationManagementSPDTO;
import my.ep.dto.cm.codification.CodificationReqDTO;
import my.ep.dto.cm.codification.CodificationResDTO;
import my.ep.persistence.entity.cm.catalog.CmBrand;
import my.ep.persistence.entity.cm.catalog.CmColor;
import my.ep.persistence.entity.cm.catalog.CmMeasurement;
import my.ep.persistence.entity.cm.catalog.CmRequestCode;
import my.ep.persistence.entity.cm.catalog.CmType;
import my.ep.persistence.entity.cm.catalog.CmUnspsc;
import my.ep.type.aut.SearchOperator;
import my.ep.web.backingbean.cm.codification.CodificationMaintenanceBackingBean;
import my.ep.web.backingbean.cm.codification.ExtensionCodeCreationBackingBean;
import my.ep.web.backingbean.cm.codification.ItemCodeCreationBackingBean;
import my.ep.web.util.lookupservice.BusinessDelegate;

/**
 * New file introduced for Audit Trail Codification Management (#28895 - #35503)
 * The backing bean for the Codification Management audit trail
 * 
 * @author Irfan
 * @since 10.09.2024
 */
public class AuditCodificationManagementBackingBean extends AuditCodificationManagementBackingBeanTemplate {
	private static final long serialVersionUID = 1L;
	private ItemCodeCreationBackingBean iccBB = null;
	private ExtensionCodeCreationBackingBean eccBB = null;
	private CodificationMaintenanceBackingBean cmBB = null;
	private transient ICodificationSO codificationSO = BusinessDelegate.getInstance().getServiceObject(
			ModuleCodeConst.CATALOGUE_MANAGEMENT, ICodificationSO.class);

	/**
	 * This constructor is to initialize
	 * ItemCodeCreationBackingBean,ExtensionCodeCreationBackingBean and
	 * CodificationMaintenanceBackingBean
	 */
	public AuditCodificationManagementBackingBean() {
		if (iccBB == null) {
			iccBB = new ItemCodeCreationBackingBean();
		}
		if (eccBB == null) {
			eccBB = new ExtensionCodeCreationBackingBean();
		}
		if (cmBB == null) {
			cmBB = new CodificationMaintenanceBackingBean();
		}
	}

	/**
	 * To insert data SP for Item Code Creation
	 */
	@Override
	public AuditCodificationManagementDTO insertItemCodeCreation(String roleCode, Long orgProfileId) {
		AuditCodificationManagementDTO auObj = new AuditCodificationManagementDTO();
		List<AuditCodificationManagementSPDTO> auSPList = new ArrayList<>();

		auSPList.addAll(insertSegment(orgProfileId, roleCode));
		auSPList.addAll(insertFamily(orgProfileId, roleCode));
		auSPList.addAll(insertClass(orgProfileId, roleCode));
		auSPList.addAll(insertCommodity(orgProfileId, roleCode));
		auObj.setListAuditSP(auSPList);
		return auObj;
	}

	/**
	 * To insert data SP for Extension Code Creation
	 */
	@Override
	public AuditCodificationManagementDTO insertExtensionCodeCreation(String roleCode, Long orgProfileId) {
		AuditCodificationManagementDTO auObj = new AuditCodificationManagementDTO();
		List<AuditCodificationManagementSPDTO> auSPList = new ArrayList<>();

		CodificationReqDTO codeReqDTO = new CodificationReqDTO();
		codeReqDTO.setUnspscCode(eccBB.getManagedBean().getUnspscCode());
		CodificationResDTO unspscObj = codificationSO.searchUnspscObj(codeReqDTO);

		for (CmRequestCode code : eccBB.getManagedBean().getAuditRequestExtList()) {

			if (code.isRequestType()) {
				CmRequestCode listCode = new CmRequestCode();
				List<CmRequestCode> typeDetails = new ArrayList<>();

				listCode.setTypeName(code.getTypeName());
				typeDetails.add(listCode);
				auObj.getTypeDetail().addAll(typeDetails);
			}

			if (code.isRequestBrand()) {
				CmRequestCode listCode = new CmRequestCode();
				List<CmRequestCode> brandDetails = new ArrayList<>();

				listCode.setBrandName(code.getBrandName());
				brandDetails.add(listCode);
				auObj.getBrandDetail().addAll(brandDetails);
			}

			if (code.isRequestMeasurement()) {
				CmRequestCode listCode = new CmRequestCode();
				List<CmRequestCode> measurementDetails = new ArrayList<>();

				listCode.setMeasurementName(code.getMeasurementName());
				measurementDetails.add(listCode);
				auObj.getMeasurementDetail().addAll(measurementDetails);
			}

			if (code.isRequestColor()) {
				CmRequestCode listCode = new CmRequestCode();
				List<CmRequestCode> colourDetails = new ArrayList<>();

				listCode.setColorName(code.getColorName());
				colourDetails.add(listCode);
				auObj.getColourDetail().addAll(colourDetails);
			}
		}

		auSPList.addAll(insertType(auObj.getTypeDetail(), orgProfileId, unspscObj, roleCode));
		auSPList.addAll(insertBrand(auObj.getBrandDetail(), orgProfileId, unspscObj, roleCode));
		auSPList.addAll(insertMeasurement(auObj.getMeasurementDetail(), orgProfileId, unspscObj, roleCode));
		auSPList.addAll(insertColour(auObj.getColourDetail(), orgProfileId, unspscObj, roleCode));
		auObj.setListAuditSP(auSPList);
		return auObj;
	}

	/**
	 * To insert data SP for Item Code Maintenance
	 */
	@Override
	public AuditCodificationManagementDTO insertItemCodeMaintenance(String roleCode, Long orgProfileId) {
		AuditCodificationManagementDTO auObj = new AuditCodificationManagementDTO();
		List<AuditCodificationManagementSPDTO> auSPList = new ArrayList<>();

		auSPList.addAll(insertSegmentMaintenance(orgProfileId, roleCode));
		auSPList.addAll(insertFamilyMaintenance(orgProfileId, roleCode));
		auSPList.addAll(insertClassMaintenance(orgProfileId, roleCode));
		auSPList.addAll(insertCommodityMaintenance(orgProfileId, roleCode));
		auObj.setListAuditSP(auSPList);
		return auObj;
	}

	/**
	 * To insert data SP for Extension Code Maintenance
	 */
	@Override
	public AuditCodificationManagementDTO insertExtensionCodeMaintenance(String roleCode, Long orgProfileId) {
		AuditCodificationManagementDTO auObj = new AuditCodificationManagementDTO();
		List<AuditCodificationManagementSPDTO> auSPList = new ArrayList<>();

		CodificationReqDTO codeReqDTO = new CodificationReqDTO();
		codeReqDTO.setUnspscCode(cmBB.getManagedBean().getAuditCommodityCode());
		CodificationResDTO unspscObj = codificationSO.searchUnspscObj(codeReqDTO);

		auSPList.addAll(insertTypeMaintenance(orgProfileId, unspscObj, roleCode));
		auSPList.addAll(insertBrandMaintenance(orgProfileId, unspscObj, roleCode));
		auSPList.addAll(insertMeasurementMaintenance(orgProfileId, unspscObj, roleCode));
		auSPList.addAll(insertColourMaintenance(orgProfileId, unspscObj, roleCode));
		auObj.setListAuditSP(auSPList);
		return auObj;
	}

	// *** Item Code ***
	// Set data in the SP for Item Code Segment Creation
	private List<AuditCodificationManagementSPDTO> insertSegment(Long orgProfileId, String roleCode) {
		List<AuditCodificationManagementSPDTO> auSPList = new ArrayList<>();
		List<List<String>> auditSegmentList = iccBB.getManagedBean().getAuditSegmentList();

		for (List<String> list : auditSegmentList) {
			AuditCodificationManagementSPDTO auSPObj = new AuditCodificationManagementSPDTO();
			CodificationReqDTO codeReqDTO = new CodificationReqDTO();
			codeReqDTO.setUnspscCode(iccBB.getManagedBean().getAuditSegmentCode());
			CodificationResDTO codeResDTO = codificationSO.searchUnspscObj(codeReqDTO);
			String newValue = list.get(0) != null ? list.get(0) : "";
			Integer fieldName = list.get(1) != null ? Integer.parseInt((String) list.get(1)) : null;
			Integer fieldLabel = list.get(2) != null ? Integer.parseInt((String) list.get(2)) : null;

			auSPObj.setRoleCode(roleCode);
			auSPObj.setOrgProfileId(orgProfileId);
			auSPObj.setDocId(codeResDTO.getUnspscObj().getUnspscId());
			auSPObj.setDocType(AuditCMConst.ITEM_CODE);
			auSPObj.setDocNo(iccBB.getManagedBean().getAuditSegmentCode());
			auSPObj.setProcessType(AuditCMConst.PROCESS_TYPE_CREATE_CI);
			auSPObj.setExtAttribute(null);
			auSPObj.setActionType(AuditCMConst.ACTION_TYPE_CREATE);
			auSPObj.setFieldName(fieldName);
			auSPObj.setUnspscId(codeResDTO.getUnspscObj().getUnspscId());
			auSPObj.setUnspscCode(iccBB.getManagedBean().getAuditSegmentCode());
			auSPObj.setOldValue(null);
			auSPObj.setNewValue(newValue);
			auSPObj.setAuGroupSecId(AuditCMConst.GROUP_SECTION_ITEM_ID);
			auSPObj.setCreatedBy(getUserIdSessionFromLoginId());
			auSPObj.setGroupSecCode(AuditCMConst.ITEM_CODE);
			auSPObj.setFieldLabelCode(String.valueOf(fieldLabel));
			auSPList.add(auSPObj);
		}
		return auSPList;
	}

	// Set data in the SP for Item Code Family Creation
	private List<AuditCodificationManagementSPDTO> insertFamily(Long orgProfileId, String roleCode) {
		List<AuditCodificationManagementSPDTO> auSPList = new ArrayList<>();
		List<List<String>> auditFamilyList = iccBB.getManagedBean().getAuditFamilyList();

		for (List<String> list : auditFamilyList) {
			AuditCodificationManagementSPDTO auSPObj = new AuditCodificationManagementSPDTO();
			CodificationReqDTO codeReqDTO = new CodificationReqDTO();
			codeReqDTO.setUnspscCode(iccBB.getManagedBean().getAuditFamilyCode());
			CodificationResDTO codeResDTO = codificationSO.searchUnspscObj(codeReqDTO);
			String newValue = list.get(0) != null ? list.get(0) : "";
			Integer fieldName = list.get(1) != null ? Integer.parseInt((String) list.get(1)) : null;
			Integer fieldLabel = list.get(2) != null ? Integer.parseInt((String) list.get(2)) : null;

			auSPObj.setRoleCode(roleCode);
			auSPObj.setOrgProfileId(orgProfileId);
			auSPObj.setDocId(codeResDTO.getUnspscObj().getUnspscId());
			auSPObj.setDocType(AuditCMConst.ITEM_CODE);
			auSPObj.setDocNo(iccBB.getManagedBean().getAuditFamilyCode());
			auSPObj.setProcessType(AuditCMConst.PROCESS_TYPE_CREATE_CI);
			auSPObj.setExtAttribute(null);
			auSPObj.setActionType(AuditCMConst.ACTION_TYPE_CREATE);
			auSPObj.setFieldName(fieldName);
			auSPObj.setUnspscId(codeResDTO.getUnspscObj().getUnspscId());
			auSPObj.setUnspscCode(iccBB.getManagedBean().getAuditFamilyCode());
			auSPObj.setOldValue(null);
			auSPObj.setNewValue(newValue);
			auSPObj.setAuGroupSecId(AuditCMConst.GROUP_SECTION_ITEM_ID);
			auSPObj.setCreatedBy(getUserIdSessionFromLoginId());
			auSPObj.setGroupSecCode(AuditCMConst.ITEM_CODE);
			auSPObj.setFieldLabelCode(String.valueOf(fieldLabel));
			auSPList.add(auSPObj);
		}
		return auSPList;
	}

	// Set data in the SP for Item Code Class Creation
	private List<AuditCodificationManagementSPDTO> insertClass(Long orgProfileId, String roleCode) {
		List<AuditCodificationManagementSPDTO> auSPList = new ArrayList<>();
		List<List<String>> auditClassList = iccBB.getManagedBean().getAuditClassList();

		for (List<String> list : auditClassList) {
			AuditCodificationManagementSPDTO auSPObj = new AuditCodificationManagementSPDTO();
			CodificationReqDTO codeReqDTO = new CodificationReqDTO();
			codeReqDTO.setUnspscCode(iccBB.getManagedBean().getAuditClassCode());
			CodificationResDTO codeResDTO = codificationSO.searchUnspscObj(codeReqDTO);
			String newValue = list.get(0) != null ? list.get(0) : "";
			Integer fieldName = list.get(1) != null ? Integer.parseInt((String) list.get(1)) : null;
			Integer fieldLabel = list.get(2) != null ? Integer.parseInt((String) list.get(2)) : null;

			auSPObj.setRoleCode(roleCode);
			auSPObj.setOrgProfileId(orgProfileId);
			auSPObj.setDocId(codeResDTO.getUnspscObj().getUnspscId());
			auSPObj.setDocType(AuditCMConst.ITEM_CODE);
			auSPObj.setDocNo(iccBB.getManagedBean().getAuditClassCode());
			auSPObj.setProcessType(AuditCMConst.PROCESS_TYPE_CREATE_CI);
			auSPObj.setExtAttribute(null);
			auSPObj.setActionType(AuditCMConst.ACTION_TYPE_CREATE);
			auSPObj.setFieldName(fieldName);
			auSPObj.setUnspscId(codeResDTO.getUnspscObj().getUnspscId());
			auSPObj.setUnspscCode(iccBB.getManagedBean().getAuditClassCode());
			auSPObj.setOldValue(null);
			auSPObj.setNewValue(newValue);
			auSPObj.setAuGroupSecId(AuditCMConst.GROUP_SECTION_ITEM_ID);
			auSPObj.setCreatedBy(getUserIdSessionFromLoginId());
			auSPObj.setGroupSecCode(AuditCMConst.ITEM_CODE);
			auSPObj.setFieldLabelCode(String.valueOf(fieldLabel));
			auSPList.add(auSPObj);
		}
		return auSPList;
	}

	// Set data in the SP for Item Code Commodity Creation
	private List<AuditCodificationManagementSPDTO> insertCommodity(Long orgProfileId, String roleCode) {
		List<AuditCodificationManagementSPDTO> auSPList = new ArrayList<>();
		List<List<String>> auditCommodityList = iccBB.getManagedBean().getAuditCommodityList();

		for (List<String> list : auditCommodityList) {
			AuditCodificationManagementSPDTO auSPObj = new AuditCodificationManagementSPDTO();
			CodificationReqDTO codeReqDTO = new CodificationReqDTO();
			codeReqDTO.setUnspscCode(iccBB.getManagedBean().getAuditCommodityCode());
			CodificationResDTO codeResDTO = codificationSO.searchUnspscObj(codeReqDTO);
			String newValue = list.get(0) != null ? list.get(0) : "";
			Integer fieldName = list.get(1) != null ? Integer.parseInt((String) list.get(1)) : null;
			Integer fieldLabel = list.get(2) != null ? Integer.parseInt((String) list.get(2)) : null;

			auSPObj.setRoleCode(roleCode);
			auSPObj.setOrgProfileId(orgProfileId);
			auSPObj.setDocId(codeResDTO.getUnspscObj().getUnspscId());
			auSPObj.setDocType(AuditCMConst.ITEM_CODE);
			auSPObj.setDocNo(iccBB.getManagedBean().getAuditCommodityCode());
			auSPObj.setProcessType(AuditCMConst.PROCESS_TYPE_CREATE_CI);
			auSPObj.setExtAttribute(null);
			auSPObj.setActionType(AuditCMConst.ACTION_TYPE_CREATE);
			auSPObj.setFieldName(fieldName);
			auSPObj.setUnspscId(codeResDTO.getUnspscObj().getUnspscId());
			auSPObj.setUnspscCode(iccBB.getManagedBean().getAuditCommodityCode());
			auSPObj.setOldValue(null);
			auSPObj.setNewValue(newValue);
			auSPObj.setAuGroupSecId(AuditCMConst.GROUP_SECTION_ITEM_ID);
			auSPObj.setCreatedBy(getUserIdSessionFromLoginId());
			auSPObj.setGroupSecCode(AuditCMConst.ITEM_CODE);
			auSPObj.setFieldLabelCode(String.valueOf(fieldLabel));
			auSPList.add(auSPObj);
		}
		return auSPList;
	}

	// Set data in the SP for Item Code Segment Maintenance
	private List<AuditCodificationManagementSPDTO> insertSegmentMaintenance(Long orgProfileId, String roleCode) {
		List<AuditCodificationManagementSPDTO> auSPList = new ArrayList<>();
		CodificationReqDTO reqDTO = new CodificationReqDTO();
		reqDTO.setUnspscCode(cmBB.getManagedBean().getAuditSegmentCode());
		CodificationResDTO resDTO = codificationSO.searchUnspscObj(reqDTO);
		List<CmUnspsc> mainSegmentList = new ArrayList<>();

		// compare
		if (resDTO.getUnspscObj() != null) {

			if (!cmBB.getManagedBean().getAuditSegmentTitle().equals(resDTO.getUnspscObj().getUnspscTitle())) {
				CmUnspsc seg = new CmUnspsc();
				List<CmUnspsc> segList = new ArrayList<>();
				seg.setOldTitle(cmBB.getManagedBean().getAuditSegmentTitle());
				seg.setNewTitle(resDTO.getUnspscObj().getUnspscTitle());
				seg.setUnspscId(resDTO.getUnspscObj().getUnspscId());
				seg.setUnspscDescription(Integer.toString(AuditCMConst.SEG_LABEL_BM_ID));
				seg.setUnspscKey(AuditCMConst.TITLE_BM_FLAG);
				segList.add(seg);
				mainSegmentList.addAll(segList);
			}

			if (!cmBB.getManagedBean().getAuditSegmentTitleEng().equals(resDTO.getUnspscObj().getUnspscTitleEng())) {
				CmUnspsc segEng = new CmUnspsc();
				List<CmUnspsc> segEngList = new ArrayList<>();
				segEng.setOldTitleEng(cmBB.getManagedBean().getAuditSegmentTitleEng());
				segEng.setNewTitleEng(resDTO.getUnspscObj().getUnspscTitleEng());
				segEng.setUnspscId(resDTO.getUnspscObj().getUnspscId());
				segEng.setUnspscDescription(Integer.toString(AuditCMConst.SEG_LABEL_BI_ID));
				segEng.setUnspscKey(AuditCMConst.TITLE_BI_FLAG);
				segEngList.add(segEng);
				mainSegmentList.addAll(segEngList);
			}
		}

		// insert SP obj
		for (CmUnspsc segObj : mainSegmentList) {
			AuditCodificationManagementSPDTO auSPObj = new AuditCodificationManagementSPDTO();
			auSPObj.setRoleCode(roleCode);
			auSPObj.setOrgProfileId(orgProfileId);
			auSPObj.setDocId(segObj.getUnspscId());
			auSPObj.setDocType(AuditCMConst.ITEM_CODE);
			auSPObj.setDocNo(cmBB.getManagedBean().getAuditSegmentCode());
			auSPObj.setProcessType(AuditCMConst.PROCESS_TYPE_MAINTENANCE_CI);
			auSPObj.setExtAttribute(null);
			auSPObj.setActionType(AuditCMConst.ACTION_TYPE_UPDATE);
			auSPObj.setUnspscId(segObj.getUnspscId());
			auSPObj.setUnspscCode(cmBB.getManagedBean().getAuditSegmentCode());
			if (segObj.getUnspscKey().equals(AuditCMConst.TITLE_BM_FLAG)) {
				auSPObj.setOldValue(segObj.getOldTitle());
				auSPObj.setNewValue(segObj.getNewTitle());
				auSPObj.setFieldLabelCode(String.valueOf(AuditCMConst.FIELD_LABEL_BM_ID_SEG));
				auSPObj.setFieldName(Integer.parseInt(segObj.getUnspscDescription()));
			} else {
				auSPObj.setOldValue(segObj.getOldTitleEng());
				auSPObj.setNewValue(segObj.getNewTitleEng());
				auSPObj.setFieldLabelCode(String.valueOf(AuditCMConst.FIELD_LABEL_BI_ID_SEG));
				auSPObj.setFieldName(Integer.parseInt(segObj.getUnspscDescription()));
			}
			auSPObj.setAuGroupSecId(AuditCMConst.GROUP_SECTION_ITEM_ID);
			auSPObj.setCreatedBy(getUserIdSessionFromLoginId());
			auSPObj.setGroupSecCode(AuditCMConst.ITEM_CODE);
			auSPList.add(auSPObj);
		}
		return auSPList;
	}

	// Set data in the SP for Item Code Family Maintenance
	private List<AuditCodificationManagementSPDTO> insertFamilyMaintenance(Long orgProfileId, String roleCode) {
		List<AuditCodificationManagementSPDTO> auSPList = new ArrayList<>();
		CodificationReqDTO reqDTO = new CodificationReqDTO();
		reqDTO.setUnspscCode(cmBB.getManagedBean().getAuditFamilyCode());
		CodificationResDTO resDTO = codificationSO.searchUnspscObj(reqDTO);
		List<CmUnspsc> mainFamilyList = new ArrayList<>();

		// compare
		if (resDTO.getUnspscObj() != null) {

			if (!cmBB.getManagedBean().getAuditFamilyTitle().equals(resDTO.getUnspscObj().getUnspscTitle())) {
				CmUnspsc fam = new CmUnspsc();
				List<CmUnspsc> famList = new ArrayList<>();
				fam.setOldTitle(cmBB.getManagedBean().getAuditFamilyTitle());
				fam.setNewTitle(resDTO.getUnspscObj().getUnspscTitle());
				fam.setUnspscId(resDTO.getUnspscObj().getUnspscId());
				fam.setUnspscDescription(Integer.toString(AuditCMConst.FAM_LABEL_BM_ID));
				fam.setUnspscKey(AuditCMConst.TITLE_BM_FLAG);
				famList.add(fam);
				mainFamilyList.addAll(famList);
			}

			if (!cmBB.getManagedBean().getAuditFamilyTitleEng().equals(resDTO.getUnspscObj().getUnspscTitleEng())) {
				CmUnspsc famEng = new CmUnspsc();
				List<CmUnspsc> famEngList = new ArrayList<>();
				famEng.setOldTitleEng(cmBB.getManagedBean().getAuditFamilyTitleEng());
				famEng.setNewTitleEng(resDTO.getUnspscObj().getUnspscTitleEng());
				famEng.setUnspscId(resDTO.getUnspscObj().getUnspscId());
				famEng.setUnspscDescription(Integer.toString(AuditCMConst.FAM_LABEL_BI_ID));
				famEng.setUnspscKey(AuditCMConst.TITLE_BI_FLAG);
				famEngList.add(famEng);
				mainFamilyList.addAll(famEngList);
			}
		}

		// insert SP obj
		for (CmUnspsc famObj : mainFamilyList) {
			AuditCodificationManagementSPDTO auSPObj = new AuditCodificationManagementSPDTO();
			auSPObj.setRoleCode(roleCode);
			auSPObj.setOrgProfileId(orgProfileId);
			auSPObj.setDocId(famObj.getUnspscId());
			auSPObj.setDocType(AuditCMConst.ITEM_CODE);
			auSPObj.setDocNo(cmBB.getManagedBean().getAuditFamilyCode());
			auSPObj.setProcessType(AuditCMConst.PROCESS_TYPE_MAINTENANCE_CI);
			auSPObj.setExtAttribute(null);
			auSPObj.setActionType(AuditCMConst.ACTION_TYPE_UPDATE);
			auSPObj.setUnspscId(famObj.getUnspscId());
			auSPObj.setUnspscCode(cmBB.getManagedBean().getAuditFamilyCode());
			if (famObj.getUnspscKey().equals(AuditCMConst.TITLE_BM_FLAG)) {
				auSPObj.setOldValue(famObj.getOldTitle());
				auSPObj.setNewValue(famObj.getNewTitle());
				auSPObj.setFieldLabelCode(String.valueOf(AuditCMConst.FIELD_LABEL_BM_ID_FAM));
				auSPObj.setFieldName(Integer.parseInt(famObj.getUnspscDescription()));
			} else {
				auSPObj.setOldValue(famObj.getOldTitleEng());
				auSPObj.setNewValue(famObj.getNewTitleEng());
				auSPObj.setFieldLabelCode(String.valueOf(AuditCMConst.FIELD_LABEL_BI_ID_FAM));
				auSPObj.setFieldName(Integer.parseInt(famObj.getUnspscDescription()));
			}
			auSPObj.setAuGroupSecId(AuditCMConst.GROUP_SECTION_ITEM_ID);
			auSPObj.setCreatedBy(getUserIdSessionFromLoginId());
			auSPObj.setGroupSecCode(AuditCMConst.ITEM_CODE);
			auSPList.add(auSPObj);
		}
		return auSPList;
	}

	// Set data in the SP for Item Code Class Maintenance
	private List<AuditCodificationManagementSPDTO> insertClassMaintenance(Long orgProfileId, String roleCode) {
		List<AuditCodificationManagementSPDTO> auSPList = new ArrayList<>();
		CodificationReqDTO reqDTO = new CodificationReqDTO();
		reqDTO.setUnspscCode(cmBB.getManagedBean().getAuditClassCode());
		CodificationResDTO resDTO = codificationSO.searchUnspscObj(reqDTO);
		List<CmUnspsc> mainClassList = new ArrayList<>();

		// compare
		if (resDTO.getUnspscObj() != null) {
			if (!cmBB.getManagedBean().getAuditClassTitle().equals(resDTO.getUnspscObj().getUnspscTitle())) {
				CmUnspsc cls = new CmUnspsc();
				List<CmUnspsc> clsList = new ArrayList<>();
				cls.setOldTitle(cmBB.getManagedBean().getAuditClassTitle());
				cls.setNewTitle(resDTO.getUnspscObj().getUnspscTitle());
				cls.setUnspscId(resDTO.getUnspscObj().getUnspscId());
				cls.setUnspscDescription(Integer.toString(AuditCMConst.CLS_LABEL_BM_ID));
				cls.setUnspscKey(AuditCMConst.TITLE_BM_FLAG);
				clsList.add(cls);
				mainClassList.addAll(clsList);
			}

			if (!cmBB.getManagedBean().getAuditClassTitleEng().equals(resDTO.getUnspscObj().getUnspscTitleEng())) {
				CmUnspsc clsEng = new CmUnspsc();
				List<CmUnspsc> clsEngList = new ArrayList<>();
				clsEng.setOldTitleEng(cmBB.getManagedBean().getAuditClassTitleEng());
				clsEng.setNewTitleEng(resDTO.getUnspscObj().getUnspscTitleEng());
				clsEng.setUnspscId(resDTO.getUnspscObj().getUnspscId());
				clsEng.setUnspscDescription(Integer.toString(AuditCMConst.CLS_LABEL_BI_ID));
				clsEng.setUnspscKey(AuditCMConst.TITLE_BI_FLAG);
				clsEngList.add(clsEng);
				mainClassList.addAll(clsEngList);
			}
		}

		// insert SP obj
		for (CmUnspsc clsObj : mainClassList) {
			AuditCodificationManagementSPDTO auSPObj = new AuditCodificationManagementSPDTO();
			auSPObj.setRoleCode(roleCode);
			auSPObj.setOrgProfileId(orgProfileId);
			auSPObj.setDocId(clsObj.getUnspscId());
			auSPObj.setDocType(AuditCMConst.ITEM_CODE);
			auSPObj.setDocNo(cmBB.getManagedBean().getAuditClassCode());
			auSPObj.setProcessType(AuditCMConst.PROCESS_TYPE_MAINTENANCE_CI);
			auSPObj.setExtAttribute(null);
			auSPObj.setActionType(AuditCMConst.ACTION_TYPE_UPDATE);
			auSPObj.setUnspscId(clsObj.getUnspscId());
			auSPObj.setUnspscCode(cmBB.getManagedBean().getAuditClassCode());
			if (clsObj.getUnspscKey().equals(AuditCMConst.TITLE_BM_FLAG)) {
				auSPObj.setOldValue(clsObj.getOldTitle());
				auSPObj.setNewValue(clsObj.getNewTitle());
				auSPObj.setFieldLabelCode(String.valueOf(AuditCMConst.FIELD_LABEL_BM_ID_CLS));
				auSPObj.setFieldName(Integer.parseInt(clsObj.getUnspscDescription()));
			} else {
				auSPObj.setOldValue(clsObj.getOldTitleEng());
				auSPObj.setNewValue(clsObj.getNewTitleEng());
				auSPObj.setFieldLabelCode(String.valueOf(AuditCMConst.FIELD_LABEL_BI_ID_CLS));
				auSPObj.setFieldName(Integer.parseInt(clsObj.getUnspscDescription()));
			}
			auSPObj.setAuGroupSecId(AuditCMConst.GROUP_SECTION_ITEM_ID);
			auSPObj.setCreatedBy(getUserIdSessionFromLoginId());
			auSPObj.setGroupSecCode(AuditCMConst.ITEM_CODE);
			auSPList.add(auSPObj);
		}
		return auSPList;
	}

	// Set data in the SP for Item Code Commodity Maintenance
	private List<AuditCodificationManagementSPDTO> insertCommodityMaintenance(Long orgProfileId, String roleCode) {
		List<AuditCodificationManagementSPDTO> auSPList = new ArrayList<>();
		CodificationReqDTO reqDTO = new CodificationReqDTO();
		reqDTO.setUnspscCode(cmBB.getManagedBean().getAuditCommodityCode());
		CodificationResDTO resDTO = codificationSO.searchUnspscObj(reqDTO);
		List<CmUnspsc> mainComList = new ArrayList<>();

		// compare
		if (resDTO.getUnspscObj() != null) {

			if (!cmBB.getManagedBean().getAuditCommodityTitle().equals(resDTO.getUnspscObj().getUnspscTitle())) {
				CmUnspsc com = new CmUnspsc();
				List<CmUnspsc> comList = new ArrayList<>();
				com.setOldTitle(cmBB.getManagedBean().getAuditCommodityTitle());
				com.setNewTitle(resDTO.getUnspscObj().getUnspscTitle());
				com.setUnspscId(resDTO.getUnspscObj().getUnspscId());
				com.setUnspscDescription(Integer.toString(AuditCMConst.COM_LABEL_BM_ID));
				com.setUnspscKey(AuditCMConst.TITLE_BM_FLAG);
				comList.add(com);
				mainComList.addAll(comList);
			}

			if (!cmBB.getManagedBean().getAuditCommodityTitleEng().equals(resDTO.getUnspscObj().getUnspscTitleEng())) {
				CmUnspsc comEng = new CmUnspsc();
				List<CmUnspsc> comEngList = new ArrayList<>();
				comEng.setOldTitleEng(cmBB.getManagedBean().getAuditCommodityTitleEng());
				comEng.setNewTitleEng(resDTO.getUnspscObj().getUnspscTitleEng());
				comEng.setUnspscId(resDTO.getUnspscObj().getUnspscId());
				comEng.setUnspscDescription(Integer.toString(AuditCMConst.COM_LABEL_BI_ID));
				comEng.setUnspscKey(AuditCMConst.TITLE_BI_FLAG);
				comEngList.add(comEng);
				mainComList.addAll(comEngList);
			}
		}

		// insert SP obj
		for (CmUnspsc comObj : mainComList) {
			AuditCodificationManagementSPDTO auSPObj = new AuditCodificationManagementSPDTO();
			auSPObj.setRoleCode(roleCode);
			auSPObj.setOrgProfileId(orgProfileId);
			auSPObj.setDocId(comObj.getUnspscId());
			auSPObj.setDocType(AuditCMConst.ITEM_CODE);
			auSPObj.setDocNo(cmBB.getManagedBean().getAuditCommodityCode());
			auSPObj.setProcessType(AuditCMConst.PROCESS_TYPE_MAINTENANCE_CI);
			auSPObj.setExtAttribute(null);
			auSPObj.setActionType(AuditCMConst.ACTION_TYPE_UPDATE);
			auSPObj.setUnspscId(comObj.getUnspscId());
			auSPObj.setUnspscCode(cmBB.getManagedBean().getAuditCommodityCode());
			if (comObj.getUnspscKey().equals(AuditCMConst.TITLE_BM_FLAG)) {
				auSPObj.setOldValue(comObj.getOldTitle());
				auSPObj.setNewValue(comObj.getNewTitle());
				auSPObj.setFieldLabelCode(String.valueOf(AuditCMConst.FIELD_LABEL_BM_ID_COM));
				auSPObj.setFieldName(Integer.parseInt(comObj.getUnspscDescription()));
			} else {
				auSPObj.setOldValue(comObj.getOldTitleEng());
				auSPObj.setNewValue(comObj.getNewTitleEng());
				auSPObj.setFieldLabelCode(String.valueOf(AuditCMConst.FIELD_LABEL_BI_ID_COM));
				auSPObj.setFieldName(Integer.parseInt(comObj.getUnspscDescription()));
			}
			auSPObj.setAuGroupSecId(AuditCMConst.GROUP_SECTION_ITEM_ID);
			auSPObj.setCreatedBy(getUserIdSessionFromLoginId());
			auSPObj.setGroupSecCode(AuditCMConst.ITEM_CODE);
			auSPList.add(auSPObj);
		}
		return auSPList;
	}

	// *** Extension Code ***
	// Set data in the SP for Extension Code Type Creation
	private List<AuditCodificationManagementSPDTO> insertType(List<CmRequestCode> listType, Long orgProfileId,
			CodificationResDTO unspscObj, String roleCode) {
		List<AuditCodificationManagementSPDTO> auSPList = new ArrayList<>();

		for (CmRequestCode objType : listType) {
			AuditCodificationManagementSPDTO auSPObj = new AuditCodificationManagementSPDTO();
			CodificationReqDTO typeReqDTO = new CodificationReqDTO();

			SearchBean<CmType> typeSearchBean = new SearchBean<>();
			typeSearchBean.getDataFilterBeans().add(
					new SearchFilterBean(AuditCMConst.AUDIT_TYPE_NAME, SearchOperator.OPERATOR_EQUALS.value(), objType
							.getTypeName()));
			typeSearchBean.getDataFilterBeans().add(
					new SearchFilterBean(AuditCMConst.AUDIT_UNSPSCID_UNSPSCID, SearchOperator.OPERATOR_EQUALS.value(),
							unspscObj.getUnspscObj().getUnspscId()));

			typeReqDTO.setTypeSearchBean(typeSearchBean);
			CodificationResDTO typeResDTO = codificationSO.searchTypeObj(typeReqDTO);

			auSPObj.setRoleCode(roleCode);
			auSPObj.setOrgProfileId(orgProfileId);
			auSPObj.setDocId(typeResDTO.getTypeList().get(0).getTypeId());
			auSPObj.setDocType(AuditCMConst.EXTENSION_CODE);
			auSPObj.setDocNo(unspscObj.getUnspscObj().getUnspscCode() + typeResDTO.getTypeList().get(0).getTypeCode());
			auSPObj.setProcessType(AuditCMConst.PROCESS_TYPE_CREATE_CE);
			auSPObj.setExtAttribute(AuditCMConst.EXTENSION_ATTRIBUTE_TYPE);
			auSPObj.setActionType(AuditCMConst.ACTION_TYPE_CREATE);
			auSPObj.setFieldName(AuditCMConst.TYPE_ID);
			auSPObj.setUnspscId(unspscObj.getUnspscObj().getUnspscId());
			auSPObj.setUnspscCode(unspscObj.getUnspscObj().getUnspscCode());
			auSPObj.setOldValue(null);
			auSPObj.setNewValue(objType.getTypeName());
			auSPObj.setAuGroupSecId(AuditCMConst.GROUP_SECTION_EXTENSION_ID);
			auSPObj.setCreatedBy(getUserIdSessionFromLoginId());
			auSPObj.setGroupSecCode(AuditCMConst.EXTENSION_CODE);
			auSPObj.setFieldLabelCode(String.valueOf(AuditCMConst.FIELD_LABEL_TYPE));
			auSPList.add(auSPObj);
		}
		return auSPList;
	}

	// Set data in the SP for Extension Code Brand Creation
	private List<AuditCodificationManagementSPDTO> insertBrand(List<CmRequestCode> listBrand, Long orgProfileId,
			CodificationResDTO unspscObj, String roleCode) {
		List<AuditCodificationManagementSPDTO> auSPList = new ArrayList<>();

		for (CmRequestCode objBrand : listBrand) {
			AuditCodificationManagementSPDTO auSPObj = new AuditCodificationManagementSPDTO();
			CodificationReqDTO brandReqDTO = new CodificationReqDTO();

			SearchBean<CmBrand> brandSearchBean = new SearchBean<>();
			brandSearchBean.getDataFilterBeans().add(
					new SearchFilterBean(AuditCMConst.AUDIT_BRAND_NAME, SearchOperator.OPERATOR_EQUALS.value(),
							objBrand.getBrandName()));
			brandSearchBean.getDataFilterBeans().add(
					new SearchFilterBean(AuditCMConst.AUDIT_UNSPSCID_UNSPSCID, SearchOperator.OPERATOR_EQUALS.value(),
							unspscObj.getUnspscObj().getUnspscId()));

			brandReqDTO.setBrandSearchBean(brandSearchBean);
			CodificationResDTO brandResDTO = codificationSO.searchBrandObj(brandReqDTO);

			auSPObj.setRoleCode(roleCode);
			auSPObj.setOrgProfileId(orgProfileId);
			auSPObj.setDocId(brandResDTO.getBrandList().get(0).getBrandId());
			auSPObj.setDocType(AuditCMConst.EXTENSION_CODE);
			auSPObj.setDocNo(unspscObj.getUnspscObj().getUnspscCode()
					+ brandResDTO.getBrandList().get(0).getBrandCode());
			auSPObj.setProcessType(AuditCMConst.PROCESS_TYPE_CREATE_CE);
			auSPObj.setExtAttribute(AuditCMConst.EXTENSION_ATTRIBUTE_BRAND);
			auSPObj.setActionType(AuditCMConst.ACTION_TYPE_CREATE);
			auSPObj.setFieldName(AuditCMConst.BRAND_ID);
			auSPObj.setUnspscId(unspscObj.getUnspscObj().getUnspscId());
			auSPObj.setUnspscCode(unspscObj.getUnspscObj().getUnspscCode());
			auSPObj.setOldValue(null);
			auSPObj.setNewValue(objBrand.getBrandName());
			auSPObj.setAuGroupSecId(AuditCMConst.GROUP_SECTION_EXTENSION_ID);
			auSPObj.setCreatedBy(getUserIdSessionFromLoginId());
			auSPObj.setGroupSecCode(AuditCMConst.EXTENSION_CODE);
			auSPObj.setFieldLabelCode(String.valueOf(AuditCMConst.FIELD_LABEL_BRAND));
			auSPList.add(auSPObj);
		}
		return auSPList;
	}

	// Set data in the SP for Extension Code Measurement Creation
	private List<AuditCodificationManagementSPDTO> insertMeasurement(List<CmRequestCode> listMeasurement,
			Long orgProfileId, CodificationResDTO unspscObj, String roleCode) {
		List<AuditCodificationManagementSPDTO> auSPList = new ArrayList<>();

		for (CmRequestCode objMeasurement : listMeasurement) {
			AuditCodificationManagementSPDTO auSPObj = new AuditCodificationManagementSPDTO();
			CodificationReqDTO measurementReqDTO = new CodificationReqDTO();

			SearchBean<CmMeasurement> measurementSearchBean = new SearchBean<>();
			measurementSearchBean.getDataFilterBeans().add(
					new SearchFilterBean(AuditCMConst.AUDIT_MEASUREMENT_NAME, SearchOperator.OPERATOR_EQUALS.value(),
							objMeasurement.getMeasurementName()));
			measurementSearchBean.getDataFilterBeans().add(
					new SearchFilterBean(AuditCMConst.AUDIT_UNSPSCID_UNSPSCID, SearchOperator.OPERATOR_EQUALS.value(),
							unspscObj.getUnspscObj().getUnspscId()));

			measurementReqDTO.setMeasurementSearchBean(measurementSearchBean);
			CodificationResDTO measurementResDTO = codificationSO.searchMeasurementObj(measurementReqDTO);

			auSPObj.setRoleCode(roleCode);
			auSPObj.setOrgProfileId(orgProfileId);
			auSPObj.setDocId(measurementResDTO.getMeasurementList().get(0).getMeasurementId());
			auSPObj.setDocType(AuditCMConst.EXTENSION_CODE);
			auSPObj.setDocNo(unspscObj.getUnspscObj().getUnspscCode()
					+ measurementResDTO.getMeasurementList().get(0).getMeasurementCode());
			auSPObj.setProcessType(AuditCMConst.PROCESS_TYPE_CREATE_CE);
			auSPObj.setExtAttribute(AuditCMConst.EXTENSION_ATTRIBUTE_MEASUREMENT);
			auSPObj.setActionType(AuditCMConst.ACTION_TYPE_CREATE);
			auSPObj.setFieldName(AuditCMConst.MEASUREMENT_ID);
			auSPObj.setUnspscId(unspscObj.getUnspscObj().getUnspscId());
			auSPObj.setUnspscCode(unspscObj.getUnspscObj().getUnspscCode());
			auSPObj.setOldValue(null);
			auSPObj.setNewValue(objMeasurement.getMeasurementName());
			auSPObj.setAuGroupSecId(AuditCMConst.GROUP_SECTION_EXTENSION_ID);
			auSPObj.setCreatedBy(getUserIdSessionFromLoginId());
			auSPObj.setGroupSecCode(AuditCMConst.EXTENSION_CODE);
			auSPObj.setFieldLabelCode(String.valueOf(AuditCMConst.FIELD_LABEL_MEASUREMENT));
			auSPList.add(auSPObj);
		}
		return auSPList;
	}

	// Set data in the SP for Extension Code Colour Creation
	private List<AuditCodificationManagementSPDTO> insertColour(List<CmRequestCode> listColour, Long orgProfileId,
			CodificationResDTO unspscObj, String roleCode) {
		List<AuditCodificationManagementSPDTO> auSPList = new ArrayList<>();

		for (CmRequestCode objColour : listColour) {
			AuditCodificationManagementSPDTO auSPObj = new AuditCodificationManagementSPDTO();
			CodificationReqDTO colourReqDTO = new CodificationReqDTO();

			SearchBean<CmColor> colourSearchBean = new SearchBean<>();
			colourSearchBean.getDataFilterBeans().add(
					new SearchFilterBean(AuditCMConst.AUDIT_COLOUR_NAME, SearchOperator.OPERATOR_EQUALS.value(),
							objColour.getColorName()));

			colourReqDTO.setColorSearchBean(colourSearchBean);
			CodificationResDTO colourResDTO = codificationSO.searchColorObj(colourReqDTO);

			auSPObj.setRoleCode(roleCode);
			auSPObj.setOrgProfileId(orgProfileId);
			auSPObj.setDocId(colourResDTO.getColorList().get(0).getColorId());
			auSPObj.setDocType(AuditCMConst.EXTENSION_CODE);
			auSPObj.setDocNo(unspscObj.getUnspscObj().getUnspscCode()
					+ colourResDTO.getColorList().get(0).getColorCode());
			auSPObj.setProcessType(AuditCMConst.PROCESS_TYPE_CREATE_CE);
			auSPObj.setExtAttribute(AuditCMConst.EXTENSION_ATTRIBUTE_COLOUR);
			auSPObj.setActionType(AuditCMConst.ACTION_TYPE_CREATE);
			auSPObj.setFieldName(AuditCMConst.COLOUR_ID);
			auSPObj.setUnspscId(unspscObj.getUnspscObj().getUnspscId());
			auSPObj.setUnspscCode(unspscObj.getUnspscObj().getUnspscCode());
			auSPObj.setOldValue(null);
			auSPObj.setNewValue(objColour.getColorName());
			auSPObj.setAuGroupSecId(AuditCMConst.GROUP_SECTION_EXTENSION_ID);
			auSPObj.setCreatedBy(getUserIdSessionFromLoginId());
			auSPObj.setGroupSecCode(AuditCMConst.EXTENSION_CODE);
			auSPObj.setFieldLabelCode(String.valueOf(AuditCMConst.FIELD_LABEL_COLOUR));
			auSPList.add(auSPObj);
		}
		return auSPList;
	}

	// Set data in the SP for Extension Code Type Maintenance
	private List<AuditCodificationManagementSPDTO> insertTypeMaintenance(Long orgProfileId,
			CodificationResDTO unspscObj, String roleCode) {
		List<AuditCodificationManagementSPDTO> auSPList = new ArrayList<>();

		for (CmType objType : cmBB.getManagedBean().getAuditTypeExtList()) {
			AuditCodificationManagementSPDTO auSPObj = new AuditCodificationManagementSPDTO();
			CodificationReqDTO typeReqDTO = new CodificationReqDTO();

			SearchBean<CmType> typeSearchBean = new SearchBean<>();
			typeSearchBean.getDataFilterBeans().add(
					new SearchFilterBean(AuditCMConst.AUDIT_TYPE_NAME, SearchOperator.OPERATOR_EQUALS.value(), objType
							.getTypeName()));
			typeSearchBean.getDataFilterBeans().add(
					new SearchFilterBean(AuditCMConst.AUDIT_UNSPSCID_UNSPSCID, SearchOperator.OPERATOR_EQUALS.value(),
							unspscObj.getUnspscObj().getUnspscId()));

			typeReqDTO.setTypeSearchBean(typeSearchBean);
			CodificationResDTO typeResDTO = codificationSO.searchTypeObj(typeReqDTO);

			auSPObj.setRoleCode(roleCode);
			auSPObj.setOrgProfileId(orgProfileId);
			auSPObj.setDocId(typeResDTO.getTypeList().get(0).getTypeId());
			auSPObj.setDocType(AuditCMConst.EXTENSION_CODE);
			auSPObj.setDocNo(unspscObj.getUnspscObj().getUnspscCode() + typeResDTO.getTypeList().get(0).getTypeCode());
			auSPObj.setProcessType(AuditCMConst.PROCESS_TYPE_MAINTENANCE_CE);
			auSPObj.setExtAttribute(AuditCMConst.EXTENSION_ATTRIBUTE_TYPE);
			auSPObj.setActionType(AuditCMConst.ACTION_TYPE_UPDATE);
			auSPObj.setFieldName(AuditCMConst.TYPE_STATUS_ID);
			auSPObj.setUnspscId(unspscObj.getUnspscObj().getUnspscId());
			auSPObj.setUnspscCode(unspscObj.getUnspscObj().getUnspscCode());
			if (objType.getRecordStatusFlg().equals(AuditCMConst.ACTIVE_STATUS)) {
				auSPObj.setOldValue(AuditCMConst.INACTIVE);
				auSPObj.setNewValue(AuditCMConst.ACTIVE);
			} else {
				auSPObj.setOldValue(AuditCMConst.ACTIVE);
				auSPObj.setNewValue(AuditCMConst.INACTIVE);
			}
			auSPObj.setAuGroupSecId(AuditCMConst.GROUP_SECTION_EXTENSION_ID);
			auSPObj.setCreatedBy(getUserIdSessionFromLoginId());
			auSPObj.setGroupSecCode(AuditCMConst.EXTENSION_CODE);
			auSPObj.setFieldLabelCode(String.valueOf(AuditCMConst.FIELD_LABEL_TYPE_STATUS));
			auSPList.add(auSPObj);
		}

		return auSPList;
	}

	// Set data in the SP for Extension Code Brand Maintenance
	private List<AuditCodificationManagementSPDTO> insertBrandMaintenance(Long orgProfileId,
			CodificationResDTO unspscObj, String roleCode) {
		List<AuditCodificationManagementSPDTO> auSPList = new ArrayList<>();

		for (CmBrand objBrand : cmBB.getManagedBean().getAuditBrandExtList()) {
			AuditCodificationManagementSPDTO auSPObj = new AuditCodificationManagementSPDTO();
			CodificationReqDTO brandReqDTO = new CodificationReqDTO();

			SearchBean<CmBrand> brandSearchBean = new SearchBean<>();
			brandSearchBean.getDataFilterBeans().add(
					new SearchFilterBean(AuditCMConst.AUDIT_BRAND_NAME, SearchOperator.OPERATOR_EQUALS.value(),
							objBrand.getBrandName()));
			brandSearchBean.getDataFilterBeans().add(
					new SearchFilterBean(AuditCMConst.AUDIT_UNSPSCID_UNSPSCID, SearchOperator.OPERATOR_EQUALS.value(),
							unspscObj.getUnspscObj().getUnspscId()));

			brandReqDTO.setBrandSearchBean(brandSearchBean);
			CodificationResDTO brandResDTO = codificationSO.searchBrandObj(brandReqDTO);

			auSPObj.setRoleCode(roleCode);
			auSPObj.setOrgProfileId(orgProfileId);
			auSPObj.setDocId(brandResDTO.getBrandList().get(0).getBrandId());
			auSPObj.setDocType(AuditCMConst.EXTENSION_CODE);
			auSPObj.setDocNo(unspscObj.getUnspscObj().getUnspscCode()
					+ brandResDTO.getBrandList().get(0).getBrandCode());
			auSPObj.setProcessType(AuditCMConst.PROCESS_TYPE_MAINTENANCE_CE);
			auSPObj.setExtAttribute(AuditCMConst.EXTENSION_ATTRIBUTE_BRAND);
			auSPObj.setActionType(AuditCMConst.ACTION_TYPE_UPDATE);
			auSPObj.setFieldName(AuditCMConst.BRAND_STATUS_ID);
			auSPObj.setUnspscId(unspscObj.getUnspscObj().getUnspscId());
			auSPObj.setUnspscCode(unspscObj.getUnspscObj().getUnspscCode());
			if (objBrand.getRecordStatusFlg().equals(AuditCMConst.ACTIVE_STATUS)) {
				auSPObj.setOldValue(AuditCMConst.INACTIVE);
				auSPObj.setNewValue(AuditCMConst.ACTIVE);
			} else {
				auSPObj.setOldValue(AuditCMConst.ACTIVE);
				auSPObj.setNewValue(AuditCMConst.INACTIVE);
			}
			auSPObj.setAuGroupSecId(AuditCMConst.GROUP_SECTION_EXTENSION_ID);
			auSPObj.setCreatedBy(getUserIdSessionFromLoginId());
			auSPObj.setGroupSecCode(AuditCMConst.EXTENSION_CODE);
			auSPObj.setFieldLabelCode(String.valueOf(AuditCMConst.FIELD_LABEL_BRAND_STATUS));
			auSPList.add(auSPObj);
		}
		return auSPList;
	}

	// Set data in the SP for Extension Code Measurement Maintenance
	private List<AuditCodificationManagementSPDTO> insertMeasurementMaintenance(Long orgProfileId,
			CodificationResDTO unspscObj, String roleCode) {
		List<AuditCodificationManagementSPDTO> auSPList = new ArrayList<>();

		for (CmMeasurement objMeasurement : cmBB.getManagedBean().getAuditMeasurementExtList()) {
			AuditCodificationManagementSPDTO auSPObj = new AuditCodificationManagementSPDTO();
			CodificationReqDTO measurementReqDTO = new CodificationReqDTO();

			SearchBean<CmMeasurement> measurementSearchBean = new SearchBean<>();
			measurementSearchBean.getDataFilterBeans().add(
					new SearchFilterBean(AuditCMConst.AUDIT_MEASUREMENT_NAME, SearchOperator.OPERATOR_EQUALS.value(),
							objMeasurement.getMeasurementName()));
			measurementSearchBean.getDataFilterBeans().add(
					new SearchFilterBean(AuditCMConst.AUDIT_UNSPSCID_UNSPSCID, SearchOperator.OPERATOR_EQUALS.value(),
							unspscObj.getUnspscObj().getUnspscId()));

			measurementReqDTO.setMeasurementSearchBean(measurementSearchBean);
			CodificationResDTO measurementResDTO = codificationSO.searchMeasurementObj(measurementReqDTO);

			auSPObj.setRoleCode(roleCode);
			auSPObj.setOrgProfileId(orgProfileId);
			auSPObj.setDocId(measurementResDTO.getMeasurementList().get(0).getMeasurementId());
			auSPObj.setDocType(AuditCMConst.EXTENSION_CODE);
			auSPObj.setDocNo(unspscObj.getUnspscObj().getUnspscCode()
					+ measurementResDTO.getMeasurementList().get(0).getMeasurementCode());
			auSPObj.setProcessType(AuditCMConst.PROCESS_TYPE_MAINTENANCE_CE);
			auSPObj.setExtAttribute(AuditCMConst.EXTENSION_ATTRIBUTE_MEASUREMENT);
			auSPObj.setActionType(AuditCMConst.ACTION_TYPE_UPDATE);
			auSPObj.setFieldName(AuditCMConst.MEASUREMENT_STATUS_ID);
			auSPObj.setUnspscId(unspscObj.getUnspscObj().getUnspscId());
			auSPObj.setUnspscCode(unspscObj.getUnspscObj().getUnspscCode());
			if (objMeasurement.getRecordStatusFlg().equals(AuditCMConst.ACTIVE_STATUS)) {
				auSPObj.setOldValue(AuditCMConst.INACTIVE);
				auSPObj.setNewValue(AuditCMConst.ACTIVE);
			} else {
				auSPObj.setOldValue(AuditCMConst.ACTIVE);
				auSPObj.setNewValue(AuditCMConst.INACTIVE);
			}
			auSPObj.setAuGroupSecId(AuditCMConst.GROUP_SECTION_EXTENSION_ID);
			auSPObj.setCreatedBy(getUserIdSessionFromLoginId());
			auSPObj.setGroupSecCode(AuditCMConst.EXTENSION_CODE);
			auSPObj.setFieldLabelCode(String.valueOf(AuditCMConst.FIELD_LABEL_MEASUREMENT_STATUS));
			auSPList.add(auSPObj);
		}
		return auSPList;
	}

	// Set data in the SP for Extension Code Colour Maintenance
	private List<AuditCodificationManagementSPDTO> insertColourMaintenance(Long orgProfileId,
			CodificationResDTO unspscObj, String roleCode) {
		List<AuditCodificationManagementSPDTO> auSPList = new ArrayList<>();

		for (CmColor objColour : cmBB.getManagedBean().getAuditColourExtList()) {
			AuditCodificationManagementSPDTO auSPObj = new AuditCodificationManagementSPDTO();
			CodificationReqDTO colourReqDTO = new CodificationReqDTO();

			SearchBean<CmColor> colourSearchBean = new SearchBean<>();
			colourSearchBean.getDataFilterBeans().add(
					new SearchFilterBean(AuditCMConst.AUDIT_COLOUR_NAME, SearchOperator.OPERATOR_EQUALS.value(),
							objColour.getColorName()));

			colourReqDTO.setColorSearchBean(colourSearchBean);
			CodificationResDTO colourResDTO = codificationSO.searchColorObj(colourReqDTO);

			auSPObj.setRoleCode(roleCode);
			auSPObj.setOrgProfileId(orgProfileId);
			auSPObj.setDocId(colourResDTO.getColorList().get(0).getColorId());
			auSPObj.setDocType(AuditCMConst.EXTENSION_CODE);
			auSPObj.setDocNo(unspscObj.getUnspscObj().getUnspscCode()
					+ colourResDTO.getColorList().get(0).getColorCode());
			auSPObj.setProcessType(AuditCMConst.PROCESS_TYPE_MAINTENANCE_CE);
			auSPObj.setExtAttribute(AuditCMConst.EXTENSION_ATTRIBUTE_COLOUR);
			auSPObj.setActionType(AuditCMConst.ACTION_TYPE_UPDATE);
			auSPObj.setFieldName(AuditCMConst.COLOUR_STATUS_ID);
			auSPObj.setUnspscId(unspscObj.getUnspscObj().getUnspscId());
			auSPObj.setUnspscCode(unspscObj.getUnspscObj().getUnspscCode());
			if (objColour.getRecordStatusFlg().equals(AuditCMConst.ACTIVE_STATUS)) {
				auSPObj.setOldValue(AuditCMConst.INACTIVE);
				auSPObj.setNewValue(AuditCMConst.ACTIVE);
			} else {
				auSPObj.setOldValue(AuditCMConst.ACTIVE);
				auSPObj.setNewValue(AuditCMConst.INACTIVE);
			}
			auSPObj.setAuGroupSecId(AuditCMConst.GROUP_SECTION_EXTENSION_ID);
			auSPObj.setCreatedBy(getUserIdSessionFromLoginId());
			auSPObj.setGroupSecCode(AuditCMConst.EXTENSION_CODE);
			auSPObj.setFieldLabelCode(String.valueOf(AuditCMConst.FIELD_LABEL_TYPE_COLOUR));
			auSPList.add(auSPObj);
		}
		return auSPList;
	}
}