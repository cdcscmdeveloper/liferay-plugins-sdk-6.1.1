package my.ep.web.backingbean.pm.audit.mapbeans;

import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import my.ep.constant.pm.AuditConst;
import my.ep.dto.pm.common.audit.AuditPMReqDTO;
import my.ep.dto.pm.common.audit.AuditPMSPDTO;
import my.ep.web.backingbean.pm.audit.AuditPMBackingBeanTemplate;
import my.ep.web.backingbean.pm.audit.role.AuditPMRoleBackingBean;
import my.ep.web.managedbean.BaseManagedBean;
import my.ep.web.managedbean.pm.settings.CalendarManageBean;

/**
 * AuditPMCalendarBackingBean
 * 
 * @author normuliana 28895_35509
 * 
 */
public class AuditPMCalendarBackingBean extends AuditPMBackingBeanTemplate {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	ArrayList<AuditPMSPDTO> objList = new ArrayList<>();
	AuditPMSPDTO detailObjTitle = new AuditPMSPDTO();
	AuditPMSPDTO detailObjFromDate = new AuditPMSPDTO();
	AuditPMSPDTO detailObjToDate = new AuditPMSPDTO();
	String pattern = "dd-MM-yyyy";
	SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);

	@Override
	public String getRoleCode() {
		AuditPMRoleBackingBean auditPMRoleBackingBean = new AuditPMRoleBackingBean();
		return auditPMRoleBackingBean.roleCalendar();
	}

	/**
	 * @param managedBeans
	 * @param roleCode
	 * @return
	 */
	@Override
	public AuditPMReqDTO insert(BaseManagedBean manageBean, String roleCode) {
		AuditPMReqDTO auditPMReqDTO = new AuditPMReqDTO();
		CalendarManageBean managedBeans = (CalendarManageBean) manageBean;

		if (managedBeans.getEvent().getId() != null) {
			updateDetail(managedBeans, auditPMReqDTO, roleCode);
		} else {
			AuditPMSPDTO dtlObjStateId = new AuditPMSPDTO();
			dtlObjStateId.setRoleCode(roleCode);
			dtlObjStateId.setDoctype(AuditConst.PM_AUD_CALENDAR);
			dtlObjStateId.setPmParameterLvlId(AuditConst.PM_AUD_CALENDAR_PMPARAMETERLVLID);
			dtlObjStateId.setDocNo(managedBeans.getEvent().getTitle());
			dtlObjStateId.setLvlCode1(AuditConst.PM_LVL_CODE_1);
			dtlObjStateId.setLvlCode2(AuditConst.PM_LVL_CODE_2_CAL);
			dtlObjStateId.setLvlCode3(AuditConst.PM_LVL_CODE_3_CAL);
			dtlObjStateId.setDocId(BigInteger.valueOf(managedBeans.getResCalendarDTO().getHoliday().getHolidayId()));
			dtlObjStateId.setFieldLabelCode(AuditConst.PM_AUD_CALENDAR_TITLE_FIELDLABELID);
			dtlObjStateId.setNewValueId(BigInteger.valueOf(managedBeans.getSelectedStateId()));
			dtlObjStateId.setActionType(AuditConst.PM_AUD_ACTION_TYPE_INS);
			objList.add(dtlObjStateId);

			detailObjTitle.setRoleCode(roleCode);
			detailObjTitle.setDoctype(AuditConst.PM_AUD_CALENDAR);
			detailObjTitle.setPmParameterLvlId(AuditConst.PM_AUD_CALENDAR_PMPARAMETERLVLID);
			detailObjTitle.setDocNo(managedBeans.getResCalendarDTO().getHoliday().getHolidayName());
			detailObjTitle.setLvlCode1(AuditConst.PM_LVL_CODE_1);
			detailObjTitle.setLvlCode2(AuditConst.PM_LVL_CODE_2_CAL);
			detailObjTitle.setLvlCode3(AuditConst.PM_LVL_CODE_3_CAL);
			detailObjTitle.setDocId(BigInteger.valueOf(managedBeans.getResCalendarDTO().getHoliday().getHolidayId()));
			detailObjTitle.setFieldLabelCode(AuditConst.PM_AUD_CALENDAR_TITLE_FIELDLABELID);
			detailObjTitle.setNewValue(managedBeans.getNewTitleValue());
			detailObjTitle.setActionType(AuditConst.PM_AUD_ACTION_TYPE_INS);
			objList.add(detailObjTitle);

			String newFrmdate = simpleDateFormat.format(managedBeans.getEvent().getStartDate());
			detailObjFromDate.setRoleCode(roleCode);
			detailObjFromDate.setDoctype(AuditConst.PM_AUD_CALENDAR);
			detailObjFromDate.setPmParameterLvlId(AuditConst.PM_AUD_CALENDAR_PMPARAMETERLVLID);
			detailObjFromDate.setDocNo(managedBeans.getResCalendarDTO().getHoliday().getHolidayName());
			detailObjFromDate.setLvlCode1(AuditConst.PM_LVL_CODE_1);
			detailObjFromDate.setLvlCode2(AuditConst.PM_LVL_CODE_2_CAL);
			detailObjFromDate.setLvlCode3(AuditConst.PM_LVL_CODE_3_CAL);
			detailObjFromDate
					.setDocId(BigInteger.valueOf(managedBeans.getResCalendarDTO().getHoliday().getHolidayId()));
			detailObjFromDate.setFieldLabelCode(AuditConst.PM_AUD_CALENDAR_FROMDATE_FIELDLABELID);
			detailObjFromDate.setNewValue(newFrmdate);
			detailObjFromDate.setActionType(AuditConst.PM_AUD_ACTION_TYPE_INS);
			objList.add(detailObjFromDate);

			String newTodate = simpleDateFormat.format(managedBeans.getEvent().getEndDate());
			detailObjToDate.setRoleCode(roleCode);
			detailObjToDate.setDoctype(AuditConst.PM_AUD_CALENDAR);
			detailObjToDate.setPmParameterLvlId(AuditConst.PM_AUD_CALENDAR_PMPARAMETERLVLID);
			detailObjToDate.setDocNo(managedBeans.getResCalendarDTO().getHoliday().getHolidayName());
			detailObjToDate.setLvlCode1(AuditConst.PM_LVL_CODE_1);
			detailObjToDate.setLvlCode2(AuditConst.PM_LVL_CODE_2_CAL);
			detailObjToDate.setLvlCode3(AuditConst.PM_LVL_CODE_3_CAL);
			detailObjToDate.setDocId(BigInteger.valueOf(managedBeans.getResCalendarDTO().getHoliday().getHolidayId()));
			detailObjToDate.setFieldLabelCode(AuditConst.PM_AUD_CALENDAR_TODATE_FIELDLABELID);
			detailObjToDate.setNewValue(newTodate);
			detailObjToDate.setActionType(AuditConst.PM_AUD_ACTION_TYPE_INS);
			objList.add(detailObjToDate);

			auditPMReqDTO.setObjList(objList);

			managedBeans.setNewTitleValue(null);
			managedBeans.setNewStartDate(null);
			managedBeans.setNewEndDate(null);
		}
		managedBeans.setDeletedEvent(false);
		return auditPMReqDTO;
	}

	private AuditPMReqDTO updateDetail(CalendarManageBean managedBeans, AuditPMReqDTO auditPMReqDTO, String roleCode) {
		if (managedBeans.getNewTitleValue() != null
				&& !managedBeans.getOldTitleValue().equalsIgnoreCase(managedBeans.getNewTitleValue())
				|| managedBeans.isDeletedEvent()) {
			detailObjTitleUpdate(managedBeans, auditPMReqDTO, roleCode);
		}

		if (managedBeans.getOldStartDate() != null && managedBeans.getNewStartDate() != null
				|| managedBeans.isDeletedEvent()) {
			detailObjFromDateUpdate(managedBeans, auditPMReqDTO, roleCode);
		}
		if (managedBeans.getOldEndDate() != null && managedBeans.getNewEndDate() != null
				|| managedBeans.isDeletedEvent()) {
			detailObjToDateUpdate(managedBeans, auditPMReqDTO, roleCode);
		}
		managedBeans.setDeletedEvent(false);

		return auditPMReqDTO;
	}

	private AuditPMReqDTO detailObjTitleUpdate(CalendarManageBean managedBeans, AuditPMReqDTO auditPMReqDTO,
			String roleCode) {
		if (!managedBeans.getOldTitleValue().equalsIgnoreCase(managedBeans.getNewTitleValue())
				|| managedBeans.isDeletedEvent()) {
			detailObjTitle.setRoleCode(roleCode);
			detailObjTitle.setDoctype(AuditConst.PM_AUD_CALENDAR);
			detailObjTitle.setPmParameterLvlId(AuditConst.PM_AUD_CALENDAR_PMPARAMETERLVLID);
			detailObjTitle.setDocNo(managedBeans.getResCalendarDTO().getHoliday().getHolidayName());
			detailObjTitle.setLvlCode1(AuditConst.PM_LVL_CODE_1);
			detailObjTitle.setLvlCode2(AuditConst.PM_LVL_CODE_2_CAL);
			detailObjTitle.setLvlCode3(AuditConst.PM_LVL_CODE_3_CAL);
			detailObjTitle.setDocId(BigInteger.valueOf(managedBeans.getResCalendarDTO().getHoliday().getHolidayId()));
			detailObjTitle.setFieldLabelCode(AuditConst.PM_AUD_CALENDAR_TITLE_FIELDLABELID);
			detailObjTitle.setOldValue(managedBeans.getOldTitleValue());
			detailObjTitle.setNewValue(!managedBeans.isDeletedEvent() ? managedBeans.getNewTitleValue() : null);
			detailObjTitle.setActionType(!managedBeans.isDeletedEvent() ? AuditConst.PM_AUD_ACTION_TYPE_UPD
					: AuditConst.PM_AUD_ACTION_TYPE_DEL);
			objList.add(detailObjTitle);
			auditPMReqDTO.setObjList(objList);
		}
		managedBeans.setOldTitleValue(null);
		managedBeans.setNewTitleValue(null);
		return auditPMReqDTO;
	}

	private AuditPMReqDTO detailObjFromDateUpdate(CalendarManageBean managedBeans, AuditPMReqDTO auditPMReqDTO,
			String roleCode) {
		String oldStartDate = simpleDateFormat.format(managedBeans.getOldStartDate());
		String newStartDate = managedBeans.getNewStartDate() != null ? simpleDateFormat.format(managedBeans
				.getNewStartDate()) : null;
		if (!oldStartDate.equalsIgnoreCase(newStartDate) || managedBeans.isDeletedEvent()) {
			detailObjFromDate.setRoleCode(roleCode);
			detailObjFromDate.setDoctype(AuditConst.PM_AUD_CALENDAR);
			detailObjFromDate.setPmParameterLvlId(AuditConst.PM_AUD_CALENDAR_PMPARAMETERLVLID);
			detailObjFromDate.setDocNo(managedBeans.getResCalendarDTO().getHoliday().getHolidayName());
			detailObjFromDate.setLvlCode1(AuditConst.PM_LVL_CODE_1);
			detailObjFromDate.setLvlCode2(AuditConst.PM_LVL_CODE_2_CAL);
			detailObjFromDate.setLvlCode3(AuditConst.PM_LVL_CODE_3_CAL);
			detailObjFromDate
					.setDocId(BigInteger.valueOf(managedBeans.getResCalendarDTO().getHoliday().getHolidayId()));
			detailObjFromDate.setFieldLabelCode(AuditConst.PM_AUD_CALENDAR_FROMDATE_FIELDLABELID);
			detailObjFromDate.setOldValue(oldStartDate);
			detailObjFromDate.setNewValue(!managedBeans.isDeletedEvent() ? newStartDate : null);
			detailObjFromDate.setActionType(!managedBeans.isDeletedEvent() ? AuditConst.PM_AUD_ACTION_TYPE_UPD
					: AuditConst.PM_AUD_ACTION_TYPE_DEL);
			objList.add(detailObjFromDate);
			auditPMReqDTO.setObjList(objList);
		}
		return auditPMReqDTO;
	}

	private AuditPMReqDTO detailObjToDateUpdate(CalendarManageBean managedBeans, AuditPMReqDTO auditPMReqDTO,
			String roleCode) {
		String oldEndDate = simpleDateFormat.format(managedBeans.getOldEndDate());
		String newEndDate = managedBeans.getNewEndDate() != null ? simpleDateFormat
				.format(managedBeans.getNewEndDate()) : null;
		if (!oldEndDate.equalsIgnoreCase(newEndDate) || managedBeans.isDeletedEvent()) {
			detailObjToDate.setRoleCode(roleCode);
			detailObjToDate.setDoctype(AuditConst.PM_AUD_CALENDAR);
			detailObjToDate.setPmParameterLvlId(AuditConst.PM_AUD_CALENDAR_PMPARAMETERLVLID);
			detailObjToDate.setDocNo(managedBeans.getResCalendarDTO().getHoliday().getHolidayName());
			detailObjToDate.setLvlCode1(AuditConst.PM_LVL_CODE_1);
			detailObjToDate.setLvlCode2(AuditConst.PM_LVL_CODE_2_CAL);
			detailObjToDate.setLvlCode3(AuditConst.PM_LVL_CODE_3_CAL);
			detailObjToDate.setDocId(BigInteger.valueOf(managedBeans.getResCalendarDTO().getHoliday().getHolidayId()));
			detailObjToDate.setFieldLabelCode(AuditConst.PM_AUD_CALENDAR_TODATE_FIELDLABELID);
			detailObjToDate.setOldValue(oldEndDate);
			detailObjToDate.setNewValue(!managedBeans.isDeletedEvent() ? newEndDate : null);
			detailObjToDate.setActionType(!managedBeans.isDeletedEvent() ? AuditConst.PM_AUD_ACTION_TYPE_UPD
					: AuditConst.PM_AUD_ACTION_TYPE_DEL);
			objList.add(detailObjToDate);
			auditPMReqDTO.setObjList(objList);

			managedBeans.setOldEndDate(null);
			managedBeans.setNewEndDate(null);
		}

		managedBeans.setOldStartDate(null);
		managedBeans.setNewStartDate(null);

		return auditPMReqDTO;
	}
}