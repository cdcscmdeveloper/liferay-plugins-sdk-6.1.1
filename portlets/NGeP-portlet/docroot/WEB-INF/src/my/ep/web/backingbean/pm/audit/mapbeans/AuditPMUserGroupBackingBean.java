package my.ep.web.backingbean.pm.audit.mapbeans;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;

import my.ep.constant.pm.AuditConst;
import my.ep.dto.pm.common.audit.AuditPMReqDTO;
import my.ep.dto.pm.common.audit.AuditPMSPDTO;
import my.ep.persistence.entity.pm.authorization.PmUserGroup;
import my.ep.persistence.entity.pm.authorization.PmUserGroupUser;
import my.ep.web.backingbean.pm.audit.AuditPMBackingBeanTemplate;
import my.ep.web.backingbean.pm.audit.role.AuditPMRoleBackingBean;
import my.ep.web.managedbean.BaseManagedBean;
import my.ep.web.managedbean.pm.usergroup.UsergroupManagedBean;

/**
 * AuditPMUserGroupBackingBean #28895-35509
 * 
 * @author normuliana
 * 
 */
public class AuditPMUserGroupBackingBean extends AuditPMBackingBeanTemplate {
	private static final long serialVersionUID = 1L;
	AuditPMReqDTO auditPMReqDTO = new AuditPMReqDTO();
	ArrayList<AuditPMSPDTO> objList = new ArrayList<>();
	String roleCode;
	String docNo;
	String docType;
	BigInteger docId;
	BigInteger docGroupId;
	String groupName;
	String groupRole;
	String ptjCodeName;
	String ptjCodeNameOld;
	String grpStatus;
	String grpStatusOld;
	String grpStatusUpdate;
	String actionType;
	BigInteger grpOrgProfileId;

	@Override
	public String getRoleCode() {
		AuditPMRoleBackingBean auditPMRoleBackingBean = new AuditPMRoleBackingBean();
		return auditPMRoleBackingBean.roleUsrGroup();
	}

	/**
	 * @param managedBeans
	 * @param roleCode
	 * @return
	 */
	@Override
	public AuditPMReqDTO insert(BaseManagedBean managedBean, String roleCode) {
		UsergroupManagedBean managedBeans = (UsergroupManagedBean) managedBean;
		PmUserGroup userGroup = managedBeans.getUserGroup();
		this.roleCode = roleCode;
		this.docGroupId = BigInteger.valueOf(userGroup.getUserGroupId());
		this.docNo = userGroup.getGroupCode();
		this.docType = userGroup.getGroupName();
		this.groupRole = userGroup.getRoleCode(); // FL_APPROVER,RN_APPROVER
		this.grpOrgProfileId = BigInteger.valueOf(userGroup.getPmOrgProfile().getOrgProfileId());

		/* update UserGroupUsers List */
		HashMap<Long, Integer> hMap = managedBeans.getHmGroupUserFlagStatDb();
		if (managedBeans.getUserGroupUpdate() != null && managedBeans.getUserGroupUpdate().getPmUserGroupUsers() != null) {
			/* update UserGroup */
			updateUserGroupListStatus(managedBeans.getUserGroupUpdate(), managedBeans.getUserGroupOld());
			for (PmUserGroupUser groupUser : managedBeans.getUserGroupUpdate().getPmUserGroupUsers()) {
				if (hMap != null && !(hMap.isEmpty())) {
					updateUserGroupList(hMap, groupUser);
				}
			}
		}

		/* new created UserGroup */
		newCreateduserGroup(managedBeans.getUserGroupNew(), managedBeans.getRoleDesc());

		/** new created UserGroupUsers List **/
		newCreateduserGroupList(managedBeans.getUserGroupNew());

		auditPMReqDTO.setObjList(objList);

		managedBeans.setUserGroup(null);
		managedBeans.setUserGroupNew(null);
		managedBeans.setUserGroupUpdate(null);
		managedBeans.setUserGroupOld(null);
		managedBeans.setHmGroupUserFlagStatDb(null);
		return auditPMReqDTO;
	}

	private void updateUserGroupList(HashMap<Long, Integer> hMap, PmUserGroupUser groupUser) {
		if (hMap.containsKey(groupUser.getPmUser().getUserId())) {
			this.actionType = AuditConst.PM_AUD_ACTION_TYPE_UPD;
			if (groupUser.getRecordStatusFlg().intValue() != hMap.get(groupUser.getPmUser().getUserId()).intValue()) {
				this.docId = BigInteger.valueOf(groupUser.getUserGroupUserId());
				detailObj(AuditConst.PM_AUD_OUSRG_USTATUS_FIELDLABELID,
						hMap.get(groupUser.getPmUser().getUserId()).intValue() == 1 ? AuditConst.PM_AUD_ACTIVE
								: AuditConst.PM_AUD_INACTIVE, groupUser.getRecordStatusFlg() == 1 ? AuditConst.PM_AUD_ACTIVE
								: AuditConst.PM_AUD_INACTIVE);
			}
		} else {
			this.actionType = AuditConst.PM_AUD_ACTION_TYPE_INS;
			this.docId = BigInteger.valueOf(groupUser.getUserGroupUserId());
			detailObj(AuditConst.PM_AUD_OUSRG_UNAME_FIELDLABELID, null, groupUser.getPmUser().getUserName());
			detailObj(AuditConst.PM_AUD_OUSRG_UIDNO_FIELDLABELID, null, groupUser.getPmUser().getIdentificationNo());
			detailObj(AuditConst.PM_AUD_OUSRG_USTATUS_FIELDLABELID, null,
					groupUser.getPmUser().getRecordStatusFlg() == 1 ? AuditConst.PM_AUD_ACTIVE : AuditConst.PM_AUD_INACTIVE);
		}
	}

	/* update UserGroup */
	private void updateUserGroupListStatus(PmUserGroup getUserGroupUpdate, PmUserGroup getUserGroupOld) {
		if (getUserGroupUpdate != null && getUserGroupOld != null) {
			String oldVal = getUserGroupOld.getRecordStatusFlg() == 1 ? AuditConst.PM_AUD_ACTIVE : AuditConst.PM_AUD_INACTIVE;
			String newVal = getUserGroupUpdate.getRecordStatusFlg() == 1 ? AuditConst.PM_AUD_ACTIVE : AuditConst.PM_AUD_INACTIVE;
			this.docId = BigInteger.valueOf(getUserGroupUpdate.getUserGroupId());
			if (oldVal != newVal) {
				this.actionType = AuditConst.PM_AUD_ACTION_TYPE_UPD;
				detailObj(AuditConst.PM_AUD_OUSRG_GSTATUS_FIELDLABELID,
						getUserGroupOld.getRecordStatusFlg() == 1 ? AuditConst.PM_AUD_ACTIVE : AuditConst.PM_AUD_INACTIVE,
						getUserGroupUpdate.getRecordStatusFlg() == 1 ? AuditConst.PM_AUD_ACTIVE : AuditConst.PM_AUD_INACTIVE);
			}
		}
	}

	/* new created UserGroup */
	private void newCreateduserGroup(PmUserGroup getUserGroupNew, String roleDesc) {
		if (getUserGroupNew != null && getUserGroupNew.getUserGroupId() > 0) {
			this.actionType = AuditConst.PM_AUD_ACTION_TYPE_INS;
			this.docId = BigInteger.valueOf(getUserGroupNew.getUserGroupId()); // /
			this.grpStatus = getUserGroupNew.getRecordStatusFlg() == 1 ? AuditConst.PM_AUD_ACTIVE : AuditConst.PM_AUD_INACTIVE;
			this.ptjCodeName = getUserGroupNew.getPmOrgProfile().getPmOrgValidity().getOrgCode() + "/"
					+ getUserGroupNew.getPmOrgProfile().getPmOrgValidity().getOrgName();
			this.groupName = getUserGroupNew.getGroupName();

			detailObj(AuditConst.PM_AUD_OUSRG_GRPNAME_FIELDLABELID, null, this.groupName);// New
			detailObj(AuditConst.PM_AUD_OUSRG_GROLE_FIELDLABELID, null, roleDesc); // New
			detailObj(AuditConst.PM_AUD_OUSRG_PTJCODENAME_FIELDLABELID, null, this.ptjCodeName);
			detailObj(AuditConst.PM_AUD_OUSRG_GSTATUS_FIELDLABELID, null, this.grpStatus);
		}
	}

	/** new created UserGroupUsers List **/
	private void newCreateduserGroupList(PmUserGroup getUserGroupNew) {
		if (getUserGroupNew != null && getUserGroupNew.getPmUserGroupUsers() != null
				&& !getUserGroupNew.getPmUserGroupUsers().isEmpty()) {
			this.actionType = AuditConst.PM_AUD_ACTION_TYPE_INS;
			for (PmUserGroupUser usersGroup : getUserGroupNew.getPmUserGroupUsers()) {
				this.docId = BigInteger.valueOf(usersGroup.getUserGroupUserId());
				detailObj(AuditConst.PM_AUD_OUSRG_UNAME_FIELDLABELID, null, usersGroup.getPmUser().getUserName());
				detailObj(AuditConst.PM_AUD_OUSRG_UIDNO_FIELDLABELID, null, usersGroup.getPmUser().getIdentificationNo());
				detailObj(AuditConst.PM_AUD_OUSRG_USTATUS_FIELDLABELID, null,
						usersGroup.getPmUser().getRecordStatusFlg() == 1 ? AuditConst.PM_AUD_ACTIVE : AuditConst.PM_AUD_INACTIVE);
			}
		}
	}

	private void detailObj(BigInteger fieldLabelId, String oldValue, String newValue) {
		AuditPMSPDTO dtlObj = new AuditPMSPDTO();
		dtlObj.setRoleCode(roleCode);
		dtlObj.setDocType(this.docType);
		if (this.groupRole != null && "RN_APPROVER".equalsIgnoreCase(this.groupRole)) {
			dtlObj.setPmParameterLvlId(AuditConst.PM_AUD_OUSRG_PMPARAMETERLVLID_RNAPPR);
		} else {
			dtlObj.setPmParameterLvlId(AuditConst.PM_AUD_OUSRG_PMPARAMETERLVLID_FULAPPR);
		}
		dtlObj.setDocNo(docNo);
		dtlObj.setGroupOrgProfileId(this.grpOrgProfileId);
		dtlObj.setLvlCode1(AuditConst.PM_LVL_CODE_1_ORGUAD);
		dtlObj.setLvlCode2(AuditConst.PM_LVL_CODE_2_OUSRG);
		dtlObj.setLvlCode3("FL_APPROVER".equalsIgnoreCase(this.groupRole) ? AuditConst.PM_LVL_CODE_3_OUSRG_FULAPPR
				: AuditConst.PM_LVL_CODE_3_OUSRG_RNAPPR);
		dtlObj.setDocId(docId);
		dtlObj.setDocGroupId(this.docGroupId);
		dtlObj.setFieldLabelCode(fieldLabelId);
		if (this.actionType == AuditConst.PM_AUD_ACTION_TYPE_UPD) {
			dtlObj.setOldValue(oldValue);
			dtlObj.setNewValue(newValue);
		} else {
			dtlObj.setNewValue(newValue);
		}
		dtlObj.setActionType(this.actionType);
		objList.add(dtlObj);
	}
}