package my.ep.web.scheduler.job.sc;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import my.ep.biz.service.osb.ntf.INtfClientSO;
import my.ep.biz.service.pm.authorization.user.IUserSO;
import my.ep.biz.service.pm.common.parameter.IParameterSO;
import my.ep.biz.service.pm.common.status.IStatusSO;
import my.ep.biz.service.sc.actor.IScActorSO;
import my.ep.biz.service.sc.quationtender.IQTDetailSO;
import my.ep.constant.ModuleCodeConst;
import my.ep.constant.NotificationTextConst;
import my.ep.constant.pm.ParameterConst;
import my.ep.constant.sc.QTStatusConst;
import my.ep.constants.CommonConstants;
import my.ep.dto.osb.ntf.NtfRequestDTO;
import my.ep.dto.osb.ntf.NtfResponseDTO;
import my.ep.dto.pm.authorization.user.UserReqDTO;
import my.ep.dto.pm.common.parameter.ParameterReqDTO;
import my.ep.dto.pm.common.status.StatusReqDTO;
import my.ep.dto.sc.actor.ScActorReqDTO;
import my.ep.dto.sc.quationtender.QuationTenderReqDTO;
import my.ep.dto.sc.quationtender.QuationTenderResDTO;
import my.ep.exception.EpException;
import my.ep.factory.EpLogger;
import my.ep.persistence.entity.pm.authorization.PmUser;
import my.ep.persistence.entity.pm.common.PmParameter;
import my.ep.persistence.entity.pm.common.PmStatusDesc;
import my.ep.persistence.entity.sc.common.ScActor;
import my.ep.type.pm.RoleCode;
import my.ep.util.lookupservice.EpEjbBusinessDelegate;
import my.ep.web.constant.sc.QTConstants;
import my.ep.web.util.lookupservice.BusinessDelegate;

import org.apache.log4j.Logger;

/**
 * The Class QuotationPropoValidReminderJob.
 */
public class QuotationPropoValidReminderJob extends BaseReminderJob {
	private static Logger log = EpLogger
			.getLogger(QuotationPropoValidReminderJob.class);

	public QuotationPropoValidReminderJob() {
		log.info("inside QuotationPropoValidReminderJob");
	}

	/** The Constant Proposal valid end at less than or equal to 30 days. */
	private static final String PROPO_VALID_END_AT_MAX_DAYS = "30";

	@Override
	public List<ReminderBean> getReminderBeans() {
		IQTDetailSO detailSO = null;
		List<ReminderBean> beans = null;
		try {
			detailSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.SUPPLIER_SELECTION, IQTDetailSO.class);
			QuationTenderReqDTO reqDTO = new QuationTenderReqDTO();
			reqDTO.setProcurementModeCode(ParameterConst.PROCUREMENT_MODE_QUOTATION);
			reqDTO.setPropoValidEndAtMaxDays(PROPO_VALID_END_AT_MAX_DAYS);
			reqDTO.setStatusIds(qtStatusListInBetween(
					QTStatusConst.QT_BRIEFING_SITE_VISIT,
					QTStatusConst.QT_PENDING_LOA));
			QuationTenderResDTO resDTO = detailSO
					.getQTDetailsByStatusIds(reqDTO);
			if (resDTO.isSuccessful()) {
				beans = new ArrayList<ReminderBean>();
				List<String> userIds = new ArrayList<String>();
				for (Object[] objects : resDTO.getQtDetailsArray()) {
					for (int i = 0; i < objects.length; i++) {
						// Date currentDate = new Date();

						// Before proposal validity expired qts
						// if(objects[7] != null &&
						// currentDate.compareTo((Date)objects[7])<0){
						ReminderBean bean = new ReminderBean();
						Date propoValidityEndDate = (Date) objects[7];
						String qtId = objects[0].toString();

						ScActor actorDO = getActorByQtIdAndRoleCode(
								Long.valueOf(qtId),
								RoleCode.DESK_OFFICER.value());
						if (actorDO != null) {
							userIds.add(actorDO.getUserId().toString());
						} else {
							log.info("user not found.");
						}
						bean.setDocNo(qtId);
						bean.setDueDate(propoValidityEndDate);
						bean.setReciepts(userIds);
						beans.add(bean);
						userIds.clear();
						// }
					}
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

	/**
	 * Send proposal validity reminder.
	 * 
	 * @param reminderDays
	 * 
	 * @param scQt
	 *            the sc qt
	 * @param senderId
	 *            the sender id
	 * @return true, if successful
	 */
	private boolean sendProposalValidityReminder(Object[] objects,
			Long totalDaysLeft) {
		log.info("sendProposalValidityReminder");
		String qtId = objects[0].toString();
		String qtNo = objects[1].toString();
		String qtTitle = objects[2].toString();
		String orgCode = objects[4].toString();
		String orgName = objects[5].toString();
		String procurementModeId = objects[6].toString();
		String propoValidEndDate = objects[7].toString();
		String publishDate = objects[8].toString();
		String status = objects[9].toString();
		String ministryName = objects[10].toString();
		boolean isQuotationTender = Boolean.FALSE;
		// NTF
		INtfClientSO ntfClientSO = null;
		NtfResponseDTO ntfResDTO = null;
		try {
			NtfRequestDTO ntfReqDTO = new NtfRequestDTO();
			ntfClientSO = (INtfClientSO) EpEjbBusinessDelegate.getInstance()
					.getServiceObject(ModuleCodeConst.NTF, INtfClientSO.class);
			ntfReqDTO.setSenderId(CommonConstants.SYSTEM_USER_ID.toString());

			ParameterReqDTO reqDTO = new ParameterReqDTO();
			reqDTO.setParameterId(Long.valueOf(procurementModeId));
			PmParameter procurementMode = getParameterSO()
					.retrieveParameterById(reqDTO).getPmParameter();

			if (procurementMode.getParameterCode().equals(
					ParameterConst.PROCUREMENT_MODE_QUOTATION)) {
				ntfReqDTO
						.setTemplateId(NotificationTextConst.TEMPLATE_ID_401163);
				isQuotationTender = Boolean.TRUE;

			} else if (procurementMode.getParameterCode().equals(
					ParameterConst.PROCUREMENT_MODE_TENDER)) {
				ntfReqDTO
						.setTemplateId(NotificationTextConst.TEMPLATE_ID_401164);
				isQuotationTender = Boolean.TRUE;
			}

			if (isQuotationTender) {
				// set parameters
				List<String> paramList = new ArrayList<String>();
				List<String> userIds = new ArrayList<String>();

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
				if (actorDO != null) {
					UserReqDTO userReqDTO = new UserReqDTO();
					userReqDTO.setUserId(actorDO.getUserId());
					PmUser pmUser = getUserSO()
							.retrieveUserByUserId(userReqDTO).getPmUser();
					if (pmUser != null) {
						preferredLanguage = pmUser.getPreferredLanguage();
					}
					userIds.add(actorDO.getUserId().toString());
				} else {
					log.info("user not found.");
				}

				StatusReqDTO statusReqDTO = new StatusReqDTO();
				statusReqDTO.setLanguageCode(preferredLanguage);
				statusReqDTO.setStatusId(Long.valueOf(status));
				PmStatusDesc statusDesc = getStatusSO().retrieveStatusDescById(
						statusReqDTO).getPmStatusDesc();
				// 7. Status
				paramList.add(statusDesc.getStatusName());

				// 8. Proposal validity due date
				paramList.add(propoValidEndDate);

				// 9. Total Days left
				paramList.add(totalDaysLeft.toString());

				if (userIds != null && userIds.size() > 0) {
					String[] userIdsArray = Arrays.copyOf(userIds.toArray(),
							userIds.size(), String[].class);
					ntfReqDTO.setUserIdArr(userIdsArray);
				} else {
					log.info("user not found.");
				}

				ntfReqDTO.setCntParaArr(paramList.toArray(new String[paramList
						.size()]));
				ntfResDTO = ntfClientSO.processAndPublish(ntfReqDTO);

				if (!NotificationTextConst.STATUS_CODE_SUCCESS.equals(ntfResDTO
						.getStatusCode())) {
					throw new EpException(ntfResDTO.getStatusDesc());
				}
			}
		} finally {
			ntfClientSO = null;
		}
		return true;
	}

	private List<Long> qtStatusListInBetween(Long fromStatusId, Long toStatusId) {
		List<Long> orderedStatuses =Arrays.asList(QTStatusConst.QT_ORDERED_STATUSES);
		List<Long> subStatusList = new ArrayList<Long>(orderedStatuses.subList(
				orderedStatuses.indexOf(fromStatusId),
				orderedStatuses.indexOf(toStatusId) + 1));
		return subStatusList;
	}

	/**
	 * Gets the parameter so.
	 * 
	 * @return the parameter so
	 */
	private IParameterSO getParameterSO() {
		return BusinessDelegate.getInstance().getServiceObject(
				ModuleCodeConst.PROFILE_MANAGEMENT, IParameterSO.class);
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
	 *            the QT id
	 * @param roleCode
	 *            the roleCode
	 * @return the scActor.
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see my.ep.web.scheduler.job.sc.BaseReminderJob#getTemplateId()
	 */
	@Override
	public String getTemplateId() {
		return NotificationTextConst.TEMPLATE_ID_401163;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see my.ep.web.scheduler.job.sc.BaseReminderJob#getParams(java.lang.Long,
	 * java.lang.Object[])
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
		List<String> userIds = new ArrayList<String>();

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
		if (actorDO != null) {
			UserReqDTO userReqDTO = new UserReqDTO();
			userReqDTO.setUserId(actorDO.getUserId());
			PmUser pmUser = getUserSO().retrieveUserByUserId(userReqDTO)
					.getPmUser();
			if (pmUser != null) {
				preferredLanguage = pmUser.getPreferredLanguage();
			}
			userIds.add(actorDO.getUserId().toString());
		} else {
			log.info("user not found.");
		}

		StatusReqDTO statusReqDTO = new StatusReqDTO();
		statusReqDTO.setLanguageCode(preferredLanguage);
		statusReqDTO.setStatusId(Long.valueOf(status));
		PmStatusDesc statusDesc = getStatusSO().retrieveStatusDescById(
				statusReqDTO).getPmStatusDesc();
		// 7. Status
		paramList.add(statusDesc.getStatusName());

		// 8. Proposal validity due date
		paramList.add(propoValidEndDate);

		// 9. Total Days left
		paramList.add(String.valueOf(reminders));

		return paramList;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see my.ep.web.scheduler.job.sc.BaseReminderJob#getKpiCode()
	 */
	@Override
	public String getKpiCode() {
		// Proposal Validity Reminder (Quotation)
		return QTConstants.KPI_CODE_QUOTATION_REMINDER;
	}
}
