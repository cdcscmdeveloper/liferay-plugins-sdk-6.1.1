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
import my.ep.web.util.lookupservice.BusinessDelegate;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

/**
 * The Class BaseReminderJob.
 * 
 * @author Sri
 * @since 16/10/2016
 */
public abstract class SourcingBaseReminder {
	private static Logger log = EpLogger.getLogger(SourcingBaseReminder.class);

	private List<ReminderBean> reminderBeans;
	private String kpiCode;

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
	protected boolean sendReminder(String templateId,
			ReminderBean reminderBean, List<String> paramList) {
		log.info("sendReminder -  Doc No " + reminderBean.getDocNo());

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
				log.info("TemplateId not found.");
			}

			// Receivers
			if (CollectionUtils.isNotEmpty(reminderBean.getReciepts())) {
				String[] userIdsArray = Arrays.copyOf(reminderBean
						.getReciepts().toArray(), reminderBean.getReciepts()
						.size(), String[].class);
				ntfReqDTO.setUserIdArr(userIdsArray);
			} else {
				log.info("Receivers not found.");
			}

			// Params.
			if (CollectionUtils.isNotEmpty(paramList)) {
				ntfReqDTO.setCntParaArr(paramList.toArray(new String[paramList
						.size()]));
			} else {
				log.info("Params not found.");
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

	protected List<Integer> getNotificationDays(String kpiCode) {
		List<Integer> reminders = new ArrayList<Integer>();
		KpiReqDTO reqDTO = new KpiReqDTO();
		reqDTO.setKpiCode(kpiCode);// days to send reminder
		KpiResDTO resDTO = getKpiSO().retrieveKpiValueByModuleCodeAndKpiCode(
				reqDTO);
		if (resDTO.isSuccessful()) {
			for (PmKPIValue value : resDTO.getKpiValues()) {
				reminders.add(value.getKpiValue());
			}
		}
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
/**
 * 
 * @param reminderDays mandatory
 * @param reminderDate mandatory
 * @return List<String> theReminderParamsList
 */
	protected abstract List<String> getParameterListForNotification(
			Long reminderDays, Date reminderDate);

	/**
	 * @return the reminderBeans
	 */
	public List<ReminderBean> getReminderBeans() {
		return reminderBeans;
	}

	/**
	 * @param reminderBeans
	 *            the reminderBeans to set
	 */
	public void setReminderBeans(List<ReminderBean> reminderBeans) {
		this.reminderBeans = reminderBeans;
	}

	/**
	 * @return the kpiCode
	 */
	public String getKpiCode() {
		return kpiCode;
	}

	/**
	 * @param kpiCode
	 *            the kpiCode to set
	 */
	public void setKpiCode(String kpiCode) {
		this.kpiCode = kpiCode;
	}

}
