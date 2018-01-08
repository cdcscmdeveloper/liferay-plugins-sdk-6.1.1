/**
 * Copyright © 2013 Archisoft. All rights reserved.
 * 
 */
package my.ep.web.scheduler.job.sc;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import my.ep.biz.service.osb.ntf.INtfClientSO;
import my.ep.biz.service.pm.common.kpi.IPmKpiSO;
import my.ep.constant.ModuleCodeConst;
import my.ep.constant.NotificationTextConst;
import my.ep.constants.CommonConstants;
import my.ep.dto.osb.ntf.NtfRequestDTO;
import my.ep.dto.osb.ntf.NtfResponseDTO;
import my.ep.dto.pm.common.kpi.KpiReqDTO;
import my.ep.dto.pm.common.kpi.KpiResDTO;
import my.ep.exception.EpException;
import my.ep.factory.EpLogger;
import my.ep.persistence.entity.pm.common.PmKPIValue;
import my.ep.util.lookupservice.EpEjbBusinessDelegate;
import my.ep.web.util.DateUtils;
import my.ep.web.util.lookupservice.BusinessDelegate;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

/**
 * The Class BaseReminderJob.
 * 
 * @author Sri
 * @since 16/10/2016
 */
public abstract class BaseReminderJob implements Job {
	private static Logger log = EpLogger.getLogger(BaseReminderJob.class);

	/**
	 * Gets the template id.
	 * 
	 * @return the template id
	 */
	public abstract String getTemplateId();

	/**
	 * Gets the kpi code.
	 * 
	 * @return the kpi code
	 */
	public abstract String getKpiCode();

	/**
	 * Gets the reminder beans.
	 * 
	 * @return the reminder beans
	 */
	public abstract List<ReminderBean> getReminderBeans();

	/**
	 * Gets the params.
	 * 
	 * @param reminderDays
	 *            the reminder days
	 * @param data
	 *            the data
	 * @return the params
	 */
	public abstract List<String> getParams(Long reminderDays, Object[] data);

	/**
	 * Gets the Subject Params.
	 * 
	 * @param data
	 *            the data
	 * @return the params
	 */
	public abstract List<String> getSubjectParams(Object[] data);

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.quartz.Job#execute(org.quartz.JobExecutionContext)
	 */
	@Override
	public void execute(JobExecutionContext arg0) throws JobExecutionException {
		log.debug("execute BaseReminderJob");
		List<ReminderBean> beans = getReminderBeans();
		List<String> paramList = null;
		if (beans != null && !beans.isEmpty()) {
			// no.of reminder days
			List<Long> notificationDays = getNotificationDays(getKpiCode());
			
			for (ReminderBean reminderBean : beans) {
				Long reminderDays = DateUtils.getDaysDifference(
						DateUtils.getCurrentDateWithoutTime(),
						reminderBean.getDueDate());

				if (CollectionUtils.isNotEmpty(notificationDays)
						&& notificationDays.contains(reminderDays)) {
					
					// params
					if(reminderBean.getData() != null){
					  paramList = getParams(reminderDays, reminderBean.getData());
					}
					
					// subparams
					List<String> subjectParamList = getSubjectParams(reminderBean
							.getData());
					
					// send reminder
					sendReminder(getTemplateId(), reminderBean, paramList,
							subjectParamList);
				}
			}
			log.debug("End BaseReminderJob");
		} else {
			log.debug("reminder beans is either null or empty");
		}

	}

	/**
	 * Send reminder.
	 * 
	 * @param templateId
	 *            the template id
	 * @param reminderBean
	 *            the reminder bean
	 * @param paramList
	 *            the param list
	 * @return true, if successful
	 */
	private boolean sendReminder(String templateId, ReminderBean reminderBean,
			List<String> paramList, List<String> subjectParamList) {
		log.debug("sendReminder -  Doc No :" + reminderBean.getDocNo());

		// NTF
		INtfClientSO ntfClientSO = null;
		NtfResponseDTO ntfResDTO = null;
		try {
			NtfRequestDTO ntfReqDTO = new NtfRequestDTO();
			ntfClientSO = (INtfClientSO) EpEjbBusinessDelegate.getInstance()
					.getServiceObject(ModuleCodeConst.NTF, INtfClientSO.class);
			ntfReqDTO.setSenderId(CommonConstants.SYSTEM_USER_ID.toString());

			// Template Id
			if (StringUtils.isNotBlank(templateId)) {
				ntfReqDTO.setTemplateId(templateId);
			} else {
				log.debug("TemplateId not found.");
			}

			// Receivers
			if (CollectionUtils.isNotEmpty(reminderBean.getReciepts())) {
				String[] userIdsArray = Arrays.copyOf(reminderBean
						.getReciepts().toArray(), reminderBean.getReciepts()
						.size(), String[].class);
				ntfReqDTO.setUserIdArr(userIdsArray);
			} else {
				log.debug("Receivers not found.");
			}

			// Params.
			if (CollectionUtils.isNotEmpty(paramList)) {
				ntfReqDTO.setCntParaArr(paramList.toArray(new String[paramList.size()]));
			} else {
				log.debug("Params not found.");
			}

			if (subjectParamList != null && !subjectParamList.isEmpty()) {
				ntfReqDTO.setSubParaArr(subjectParamList.toArray(new String[subjectParamList.size()]));
			} else {
				log.debug("Subject params not found.");
			}

			ntfResDTO = ntfClientSO.processAndPublish(ntfReqDTO);

			if (!NotificationTextConst.STATUS_CODE_SUCCESS.equals(ntfResDTO
					.getStatusCode())) {
				throw new EpException(ntfResDTO.getStatusDesc());
			}
			// }
		} finally {
			ntfClientSO = null;
		}
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see my.ep.web.scheduler.job.sc.BaseReminderJob#getNotificationDays()
	 */
	public List<Long> getNotificationDays(String kpiCode) {
		List<Long> reminders = new ArrayList<Long>();
		KpiReqDTO reqDTO = new KpiReqDTO();
		reqDTO.setKpiCode(kpiCode);// days to send reminder
		KpiResDTO resDTO = getKpiSO().retrieveKpiValueByModuleCodeAndKpiCode(
				reqDTO);
		if (resDTO.isSuccessful()) {
			for (PmKPIValue value : resDTO.getKpiValues()) {
				reminders.add(value.getKpiValue().longValue());
			}
		}
		log.debug("reminders :"+reminders.size());
		return reminders;
	}

	private IPmKpiSO getKpiSO() {
		return BusinessDelegate.getInstance().getServiceObject(
				ModuleCodeConst.PROFILE_MANAGEMENT, IPmKpiSO.class);
	}

	/**
	 * The Class ReminderBean.
	 */
	protected class ReminderBean {
		private String docNo;
		private Date dueDate;
		private Object[] data;
		private List<String> reciepts;

		/**
		 * @return the docNo
		 */
		public String getDocNo() {
			return docNo;
		}

		/**
		 * @param docNo
		 *            the docNo to set
		 */
		public void setDocNo(String docNo) {
			this.docNo = docNo;
		}

		/**
		 * @return the dueDate
		 */
		public Date getDueDate() {
			return dueDate;
		}

		/**
		 * @param dueDate
		 *            the dueDate to set
		 */
		public void setDueDate(Date dueDate) {
			this.dueDate = dueDate;
		}

		/**
		 * @return the data
		 */
		public Object[] getData() {
			return data;
		}

		/**
		 * @param data
		 *            the data to set
		 */
		public void setData(Object[] data) {
			this.data = data;
		}

		/**
		 * @return the receipts
		 */
		public List<String> getReciepts() {
			return reciepts;
		}

		/**
		 * @param reciepts
		 *            the receipts to set
		 */
		public void setReciepts(List<String> reciepts) {
			this.reciepts = reciepts;
		}
	}
}
