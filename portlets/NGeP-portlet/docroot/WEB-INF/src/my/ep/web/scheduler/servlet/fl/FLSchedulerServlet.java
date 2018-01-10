/**
 * Copyright © 2013 Archisoft. All rights reserved.
 */
package my.ep.web.scheduler.servlet.fl;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;

import my.ep.constant.EpPropertiesConst;
import my.ep.factory.EpLogger;
import my.ep.util.SchedulerPropertiesUtils;
import my.ep.web.scheduler.constants.common.SchedulerConstants;
import my.ep.web.scheduler.helper.EpSchedulerHelper;
import my.ep.web.scheduler.job.gfmas.PaymentInformationJob;
import my.ep.web.scheduler.job.phis.PaymentEffectNotificationJob;

import org.apache.log4j.Logger;
import org.quartz.JobKey;

/**
 * The Class FLSchedulerServlet.
* 
 * @author Giribabu
 * @since 18/2/2016
 */
public class FLSchedulerServlet extends HttpServlet {
	private static final long serialVersionUID = 5961523576286300095L;
	public static Logger log = EpLogger.getLogger(FLSchedulerServlet.class);

	/* (non-Javadoc)
	 * @see javax.servlet.GenericServlet#init(javax.servlet.ServletConfig)
	 */
	public void init(ServletConfig config) throws ServletException {

		// Expire the contract that no more effective.
		EpSchedulerHelper
				.scheduleJob(
						new JobKey(
								SchedulerConstants.GFMAS_PAYMENT_INFORMATION_INBOUND,
								SchedulerConstants.GFMAS_INTEGRATION_JOB_GROUP),
						PaymentInformationJob.class,
						SchedulerConstants.GFMAS_FL_IN_JOB_DESC,
						SchedulerPropertiesUtils
								.getInstance()
								.getProperty(
										EpPropertiesConst.SCHEDULER_JOB_CRONEXPRESSION_FL_1GFMAS_FL_INBOUND));

		EpSchedulerHelper
				.scheduleJob(
						new JobKey(SchedulerConstants.PHIS_PAY_EFF_NOTIFY_JOB,
								SchedulerConstants.PHIS_INTEGRATION_JOB_GROUP),
						PaymentEffectNotificationJob.class,
						SchedulerConstants.PHIS_FL_JOB_DESC,
						SchedulerPropertiesUtils
								.getInstance()
								.getProperty(
										EpPropertiesConst.SCHEDULER_JOB_CRONEXPRESSION_FL_PHIS_FL_OUTBOUND));
	}

}
