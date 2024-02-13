package my.ep.web.backingbean.pm.audit.calendar;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import my.ep.constant.audit.AuditConst;
import my.ep.dto.pm.common.audit.calendar.AuPmCalendarDTO;
import my.ep.dto.pm.common.audit.calendar.AuPmCalendarDetailSPDTO;
import my.ep.dto.pm.common.audit.calendar.AuPmCalendarMasterSPDTO;
import my.ep.web.managedbean.BaseManagedBean;
import my.ep.web.managedbean.pm.settings.CalendarManageBean;

public class AuditCalendarBackingBean extends AuditPMCalendarBackingBeanTemplate {
	private static final long serialVersionUID = 1L;

	public AuPmCalendarDTO masterInsert(AuPmCalendarDTO au, Long userId, BaseManagedBean manageBean, String roleCode) {
		CalendarManageBean calManagedBean = (CalendarManageBean) manageBean;
		AuPmCalendarDTO auPMObj = (AuPmCalendarDTO) au;
		ArrayList<AuPmCalendarMasterSPDTO> masterObjlist = new ArrayList<AuPmCalendarMasterSPDTO>();
		AuPmCalendarMasterSPDTO msterObj = new AuPmCalendarMasterSPDTO();

		msterObj.setRoleCode(roleCode);
		msterObj.setDoctype(AuditConst.PM_AUD_CALENDAR);
		msterObj.setPmParameterLvlId(AuditConst.PM_AUD_CALENDAR_PMPARAMETERLVLID);
		msterObj.setDocNo(calManagedBean.getResCalendarDTO().getHoliday().getHolidayName());
		msterObj.setPkId(calManagedBean.getResCalendarDTO().getHoliday().getHolidayId());
		msterObj.setColumnName(AuditConst.PM_AUD_CALENDAR_SOURCECOLUMN);
		masterObjlist.add(msterObj);

		auPMObj.setCalendarMasterObjlist(masterObjlist);

		return auPMObj;
	}

	public AuPmCalendarDTO detailsInsert(BaseManagedBean managedBean, Long userId) {
		CalendarManageBean calManagedBean = (CalendarManageBean) managedBean;
		AuPmCalendarDTO auPMObj = new AuPmCalendarDTO();
		auPMObj = insertDetailVal(auPMObj, calManagedBean, userId);

		return auPMObj;
	}

	List<AuPmCalendarDetailSPDTO> detailObjlist = new ArrayList<AuPmCalendarDetailSPDTO>();
	ArrayList<AuPmCalendarDetailSPDTO> dtlObjlist = new ArrayList<AuPmCalendarDetailSPDTO>();
	AuPmCalendarDetailSPDTO dtlObjTitle = new AuPmCalendarDetailSPDTO();
	AuPmCalendarDetailSPDTO dtlObjFromDate = new AuPmCalendarDetailSPDTO();
	AuPmCalendarDetailSPDTO dtlObjToDate = new AuPmCalendarDetailSPDTO();

	private AuPmCalendarDTO insertDetailVal(AuPmCalendarDTO auPMObj, CalendarManageBean calManagedBean, Long userId) {
		String pattern = "dd-MM-yyyy";
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
		if (calManagedBean.getEvent().getId() != null) {
			if (calManagedBean.getNewTitleValue() != null
					&& !calManagedBean.getOldTitleValue().equalsIgnoreCase(calManagedBean.getNewTitleValue())
					|| calManagedBean.isDeletedEvent() == true) {
				dtlObjTitle.setDocId(calManagedBean.getResCalendarDTO().getHoliday().getHolidayId().intValue());
				dtlObjTitle.setPmFieldLabelid(AuditConst.PM_AUD_CALENDAR_TITLE_FIELDLABELID);
				dtlObjTitle.setOldValue(calManagedBean.getOldTitleValue());
				dtlObjTitle.setNewValue(calManagedBean.isDeletedEvent() == false ? calManagedBean.getNewTitleValue() : null);
				dtlObjTitle.setActionType(calManagedBean.isDeletedEvent() == false ? AuditConst.PM_AUD_ACTION_TYPE_UPD : AuditConst.PM_AUD_ACTION_TYPE_DEL);
				dtlObjlist.add(dtlObjTitle);
				auPMObj.setCalendarDetailObjlist(dtlObjlist);

				calManagedBean.setOldTitleValue(null);
				calManagedBean.setNewTitleValue(null);				
			}
			if (calManagedBean.getOldStartDate() != null && calManagedBean.getNewStartDate() != null || calManagedBean.isDeletedEvent() == true) {
				String oldStartDate = simpleDateFormat.format(calManagedBean.getOldStartDate());
				String newStartDate = calManagedBean.getNewStartDate() != null ? simpleDateFormat.format(calManagedBean.getNewStartDate()) : null;
				if (!oldStartDate.equalsIgnoreCase(newStartDate) || calManagedBean.isDeletedEvent() == true) {
					dtlObjFromDate.setDocId(calManagedBean.getResCalendarDTO().getHoliday().getHolidayId().intValue());
					dtlObjFromDate.setPmFieldLabelid(AuditConst.PM_AUD_CALENDAR_FROMDATE_FIELDLABELID);
					dtlObjFromDate.setOldValue(oldStartDate);
					dtlObjFromDate.setNewValue(calManagedBean.isDeletedEvent() == false ? newStartDate : null);
					dtlObjFromDate.setActionType(calManagedBean.isDeletedEvent() == false ? AuditConst.PM_AUD_ACTION_TYPE_UPD : AuditConst.PM_AUD_ACTION_TYPE_DEL);
					dtlObjlist.add(dtlObjFromDate);
					auPMObj.setCalendarDetailObjlist(dtlObjlist);

					calManagedBean.setOldStartDate(null);
					calManagedBean.setNewStartDate(null);
				}
			}
			if (calManagedBean.getOldEndDate() != null && calManagedBean.getNewEndDate() != null || calManagedBean.isDeletedEvent() == true) {
				String oldEndDate = simpleDateFormat.format(calManagedBean.getOldEndDate());
				String newEndDate = calManagedBean.getNewEndDate() != null ? simpleDateFormat.format(calManagedBean.getNewEndDate()): null;
				if (!oldEndDate.equalsIgnoreCase(newEndDate) || calManagedBean.isDeletedEvent() == true) {
					dtlObjToDate.setDocId(calManagedBean.getResCalendarDTO().getHoliday().getHolidayId().intValue());
					dtlObjToDate.setPmFieldLabelid(AuditConst.PM_AUD_CALENDAR_TODATE_FIELDLABELID);
					dtlObjToDate.setOldValue(oldEndDate);
					dtlObjToDate.setNewValue(calManagedBean.isDeletedEvent() == false ? newEndDate : null);
					dtlObjToDate.setActionType(calManagedBean.isDeletedEvent() == false ? AuditConst.PM_AUD_ACTION_TYPE_UPD : AuditConst.PM_AUD_ACTION_TYPE_DEL);
					dtlObjlist.add(dtlObjToDate);
					auPMObj.setCalendarDetailObjlist(dtlObjlist);

					calManagedBean.setOldEndDate(null);
					calManagedBean.setNewEndDate(null);
				}				
			}
			calManagedBean.setDeletedEvent(false);
		} else {
			AuPmCalendarDetailSPDTO dtlObjStateId = new AuPmCalendarDetailSPDTO();
			dtlObjStateId.setDocId(calManagedBean.getResCalendarDTO().getHoliday().getHolidayId().intValue());
			dtlObjStateId.setPmFieldLabelid(AuditConst.PM_AUD_CALENDAR_TITLE_FIELDLABELID);
			dtlObjStateId.setNewValueId(calManagedBean.getSelectedStateId().intValue());
			dtlObjStateId.setActionType(AuditConst.PM_AUD_ACTION_TYPE_INS);
			dtlObjlist.add(dtlObjStateId);

			dtlObjTitle.setDocId(calManagedBean.getResCalendarDTO().getHoliday().getHolidayId().intValue());
			dtlObjTitle.setPmFieldLabelid(AuditConst.PM_AUD_CALENDAR_TITLE_FIELDLABELID);
			dtlObjTitle.setNewValue(calManagedBean.getNewTitleValue());
			dtlObjTitle.setActionType(AuditConst.PM_AUD_ACTION_TYPE_INS);
			dtlObjlist.add(dtlObjTitle);

			String newFrmdate = simpleDateFormat.format(calManagedBean.getEvent().getStartDate());
			dtlObjFromDate.setDocId(calManagedBean.getResCalendarDTO().getHoliday().getHolidayId().intValue());
			dtlObjFromDate.setPmFieldLabelid(AuditConst.PM_AUD_CALENDAR_FROMDATE_FIELDLABELID);
			dtlObjFromDate.setNewValue(newFrmdate);
			dtlObjFromDate.setActionType(AuditConst.PM_AUD_ACTION_TYPE_INS);
			dtlObjlist.add(dtlObjFromDate);

			String newTodate = simpleDateFormat.format(calManagedBean.getEvent().getEndDate());
			dtlObjToDate.setDocId(calManagedBean.getResCalendarDTO().getHoliday().getHolidayId().intValue());
			dtlObjToDate.setPmFieldLabelid(AuditConst.PM_AUD_CALENDAR_TODATE_FIELDLABELID);
			dtlObjToDate.setNewValue(newTodate);
			dtlObjToDate.setActionType(AuditConst.PM_AUD_ACTION_TYPE_INS);
			dtlObjlist.add(dtlObjToDate);

			auPMObj.setCalendarDetailObjlist(dtlObjlist);
			
			calManagedBean.setNewTitleValue(null);
			calManagedBean.setNewStartDate(null);
			calManagedBean.setNewEndDate(null);
		}
		return auPMObj;
	}
}
