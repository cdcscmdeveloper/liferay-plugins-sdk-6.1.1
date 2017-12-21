package my.ep.web.scheduler.job.sc;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import my.ep.biz.service.pm.authorization.user.IUserSO;
import my.ep.biz.service.pm.common.kpi.IPmKpiSO;
import my.ep.biz.service.pm.common.status.IStatusSO;
import my.ep.biz.service.sc.actor.IScActorSO;
import my.ep.biz.service.sc.quationtender.IQTDetailSO;
import my.ep.constant.ModuleCodeConst;
import my.ep.constant.NotificationTextConst;
import my.ep.constant.pm.ParameterConst;
import my.ep.constant.sc.QTStatusConst;
import my.ep.constants.CommonConstants;
import my.ep.dto.pm.authorization.user.UserReqDTO;
import my.ep.dto.pm.common.kpi.KpiReqDTO;
import my.ep.dto.pm.common.kpi.KpiResDTO;
import my.ep.dto.pm.common.status.StatusReqDTO;
import my.ep.dto.sc.actor.ScActorReqDTO;
import my.ep.dto.sc.quationtender.QuationTenderReqDTO;
import my.ep.dto.sc.quationtender.QuationTenderResDTO;
import my.ep.factory.EpLogger;
import my.ep.persistence.entity.pm.authorization.PmUser;
import my.ep.persistence.entity.pm.common.PmStatusDesc;
import my.ep.persistence.entity.sc.common.ScActor;
import my.ep.type.pm.RoleCode;
import my.ep.util.lookupservice.EpEjbBusinessDelegate;
import my.ep.web.constant.sc.QTConstants;
import my.ep.web.util.lookupservice.BusinessDelegate;

import org.apache.log4j.Logger;

/**
 * The Class TenderPropoValidReminderJob.
 */
public class TenderPropoValidReminderJob extends BaseReminderJob{
	private static Logger log = EpLogger
			.getLogger(TenderPropoValidReminderJob.class);

	public TenderPropoValidReminderJob() {
		log.info("inside TenderPropoValidReminderJob");
	}

	@Override
	public List<ReminderBean> getReminderBeans() {
		log.info("Tender getReminderBeans");
		IQTDetailSO detailSO = null;
		List<ReminderBean> beans = null;
		try {
			detailSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.SUPPLIER_SELECTION, IQTDetailSO.class);
			QuationTenderReqDTO reqDTO = new QuationTenderReqDTO();
			reqDTO.setProcurementModeCode(ParameterConst.PROCUREMENT_MODE_TENDER);
			reqDTO.setPropoValidEndAtMaxDays(getMaxProposalValidDays());
			log.info("max propo validity days :"+reqDTO.getPropoValidEndAtMaxDays());
			reqDTO.setStatusIds(qtStatusListInBetween(
					QTStatusConst.QT_BRIEFING_SITE_VISIT,
					QTStatusConst.QT_PENDING_LOA));
			QuationTenderResDTO resDTO = detailSO
					.getQTDetailsByStatusIds(reqDTO);

			log.info("Tender Reminder @ QT List Size : "+resDTO.getQtDetailsArray().size());

			if (resDTO.isSuccessful()) {
				beans = new ArrayList<ReminderBean>();

				for (Object[] objects : resDTO.getQtDetailsArray()) {
					List<String> userIds = new ArrayList<String>();
					ReminderBean bean = new ReminderBean();
					Date propoValidityEndDate = (Date) objects[7];
					String qtId = objects[0].toString();

					ScActor actorDO = getActorByQtIdAndRoleCode(
							Long.valueOf(qtId),
							RoleCode.DESK_OFFICER.value());
					if (actorDO.getUserId() != null) {
						userIds.add(actorDO.getUserId().toString());
					} else {
						log.info("user not found for qt id "+qtId);
					}
					bean.setDocNo(objects[1].toString());
					bean.setDueDate(propoValidityEndDate);
					bean.setReciepts(userIds);
					bean.setData(objects);
					beans.add(bean);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getLocalizedMessage());

		} finally {
			detailSO = null;

		}
		return beans;
	}

	private List<Long> qtStatusListInBetween(Long fromStatusId, Long toStatusId) {
		List<Long> orderedStatuses = new ArrayList<Long>(Arrays.asList(QTStatusConst.QT_ORDERED_STATUSES));
		List<Long> subStatuses = new ArrayList<Long>(orderedStatuses.subList(
				orderedStatuses.indexOf(fromStatusId), 
				orderedStatuses.indexOf(toStatusId)+1));
		return subStatuses;
	}

	/**
	 * Gets the user so.
	 * 
	 * @return the user so
	 */
	private IUserSO getUserSO() {
		return BusinessDelegate.getInstance().getServiceObject(
				ModuleCodeConst.PROFILE_MANAGEMENT, IUserSO.class);
	}

	/**
	 * Gets the status so.
	 * 
	 * @return the status so
	 */
	private IStatusSO getStatusSO() {
		return BusinessDelegate.getInstance().getServiceObject(
				ModuleCodeConst.PROFILE_MANAGEMENT, IStatusSO.class);
	}

	/**
	 * Retrieve ScActor by qtId and roleCode
	 * 
	 * @param qtId
	 * 		the QT id
	 * @param roleCode
	 * 		the roleCode
	 * @return	the scActor.
	 */	
	private ScActor getActorByQtIdAndRoleCode(Long qtId, String roleCode) {
		IScActorSO actorSO = null;
		try {
			actorSO = EpEjbBusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.SUPPLIER_SELECTION, IScActorSO.class);

			ScActorReqDTO reqDTO = new ScActorReqDTO();
			reqDTO.setDocType(ParameterConst.DOCTYPE_QUOTATION);
			reqDTO.setDocId(qtId);
			reqDTO.setRoleCode(roleCode);

			ScActor scActor = actorSO.retrieveActorByTypeIdAndRoleCode(reqDTO)
					.getScActor();

			if (scActor == null) {
				scActor = new ScActor();
			}
			return scActor;
		} finally {
			actorSO = null;
		}		
	}

	/* (non-Javadoc)
	 * @see my.ep.web.scheduler.job.sc.BaseReminderJob#getTemplateId()
	 */
	@Override
	public String getTemplateId() {
		return NotificationTextConst.TEMPLATE_ID_401164;
	}

	/* (non-Javadoc)
	 * @see my.ep.web.scheduler.job.sc.BaseReminderJob#getParams(java.lang.Long, java.lang.Object[])
	 */
	@Override
	public List<String> getParams(Long reminders, Object[] data) {
		String qtId = data[0].toString();
		String qtNo = data[1].toString();
		String qtTitle = data[2].toString();
		String orgCode = data[4].toString();
		String orgName = data[5].toString();
		String propoValidEndDate = data[7].toString();
		String publishDate = data[8].toString();
		String status = data[9].toString();
		String ministryName = data[10].toString();

		List<String> paramList = new ArrayList<String>();

		// 1. Ministry Name        
		paramList.add(ministryName);

		// 2. PTJ Code
		paramList.add(orgCode);

		// 3. PTJ Name
		paramList.add(orgName);

		// 4. qtNo
		paramList.add(qtNo);

		// 5. Qt Title in header
		paramList.add(qtTitle);

		// 6. Publish Date/ Time    
		paramList.add(publishDate);

		String preferredLanguage = CommonConstants.DEFAULT_LANGUAGE_CODE;
		ScActor actorDO = getActorByQtIdAndRoleCode(Long.valueOf(qtId),
				RoleCode.DESK_OFFICER.value());
		if(actorDO != null) {
			UserReqDTO userReqDTO = new UserReqDTO();
			userReqDTO.setUserId(actorDO.getUserId());
			PmUser pmUser = getUserSO().retrieveUserByUserId(userReqDTO).getPmUser();
			if(pmUser != null) {
				preferredLanguage = pmUser.getPreferredLanguage();
			}
		} else {
			log.info("user not found.");
		}

		StatusReqDTO statusReqDTO = new StatusReqDTO();
		statusReqDTO.setLanguageCode(preferredLanguage);
		statusReqDTO.setStatusId(Long.valueOf(status));
		PmStatusDesc statusDesc = getStatusSO().retrieveStatusDescById(statusReqDTO).getPmStatusDesc();
		// 7. Status
		paramList.add(statusDesc.getStatusName());

		// 8. Proposal validity due date
		paramList.add(propoValidEndDate);

		// 9. Total Days left 
		paramList.add(String.valueOf(reminders));
		log.info("Tender paramList Size : "+paramList.size());
		return paramList;
	}
	
	/* (non-Javadoc)
	 * @see my.ep.web.scheduler.job.sc.BaseReminderJob#getKpiCode()
	 */
	@Override
	public String getKpiCode() {
		// Proposal Validity Reminder (Tender)
		return QTConstants.KPI_CODE_TENDER_REMINDER;
	}
	
	/**
	 * Gets the max proposal valid days.
	 *
	 * @return the max proposal valid days
	 */
	public Integer getMaxProposalValidDays() {
		KpiReqDTO reqDTO = new KpiReqDTO();
		reqDTO.setMaxKPIValue(Boolean.TRUE);
		reqDTO.setKpiCode(getKpiCode());// days to send reminder
		KpiResDTO resDTO = getKpiSO().retrieveKpiValueByModuleCodeAndKpiCode(
				reqDTO);
		if (resDTO.isSuccessful()) {
			if(resDTO.getKpiValue() != null){
				return resDTO.getKpiValue().getKpiValue();
			}
		}
		return null;
	}
	
	private IPmKpiSO getKpiSO() {
		return BusinessDelegate.getInstance().getServiceObject(
				ModuleCodeConst.PROFILE_MANAGEMENT, IPmKpiSO.class);
	}

	@Override
	public List<String> getSubjectParams(Object[] data) {
		// TODO Auto-generated method stub
		return null;
	}

}
