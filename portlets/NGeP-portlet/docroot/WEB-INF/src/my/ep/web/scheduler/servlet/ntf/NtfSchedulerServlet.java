package my.ep.web.scheduler.servlet.ntf;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;

import my.ep.factory.EpLogger;
import my.ep.web.scheduler.constants.common.SchedulerConstants;
import my.ep.web.scheduler.helper.EpSchedulerHelper;
import my.ep.web.scheduler.job.ntf.NtfEmailBulkJob;
import my.ep.web.scheduler.job.ntf.NtfEmailRealTimeJob;
import my.ep.web.scheduler.job.ntf.NtfMessageBulkJob;
import my.ep.web.scheduler.job.ntf.NtfMessageDirectEmailJob;
import my.ep.web.scheduler.job.ntf.NtfMessageRealTimelJob;

import org.apache.log4j.Logger;
import org.quartz.JobKey;

public class NtfSchedulerServlet extends HttpServlet {
	
	public static Logger log = EpLogger.getLogger(NtfSchedulerServlet.class);

	public void init(ServletConfig config) throws ServletException {

		EpSchedulerHelper.scheduleJob(new JobKey(
				SchedulerConstants.NTF_MESSAGE_BULK_INTERNAL_JOB,
				SchedulerConstants.NTF_MESSAGE_BULK_INTERNAL_JOB),
				NtfMessageBulkJob.class,
				SchedulerConstants.NTF_MESSAGE_BULK_JOB_DESC, "0 0/1 * * * ?");
		
		EpSchedulerHelper.scheduleJob(new JobKey(
				SchedulerConstants.NTF_MESSAGE_REAL_TIME_INTERNAL_JOB,
				SchedulerConstants.NTF_MESSAGE_REAL_TIME_JOB_GROUP),
				NtfMessageRealTimelJob.class,
				SchedulerConstants.NTF_MESSAGE_REAL_TIME_JOB_DESC, "0 0/1 * * * ?");
		
		EpSchedulerHelper.scheduleJob(new JobKey(
				SchedulerConstants.NTF_MESSAGE_DIRECT_EMAIL_INTERNAL_JOB,
				SchedulerConstants.NTF_MESSAGE_DIRECT_EMAIL_JOB_GROUP),
				NtfMessageDirectEmailJob.class,
				SchedulerConstants.NTF_MESSAGE_DIRECT_EMAIL_JOB_DESC, "0 0/1 * * * ?");
		
		EpSchedulerHelper.scheduleJob(new JobKey(
				SchedulerConstants.NTF_EMAIL_BULK_INTERNAL_JOB,
				SchedulerConstants.NTF_EMAIL_BULK_JOB_GROUP),
				NtfEmailBulkJob.class,
				SchedulerConstants.NTF_EMAIL_BULK_JOB_DESC, "0 0/60 * * * ?");
		
		EpSchedulerHelper.scheduleJob(new JobKey(
				SchedulerConstants.NTF_EMAIL_REAL_TIME_INTERNAL_JOB,
				SchedulerConstants.NTF_EMAIL_REAL_TIME_JOB_GROUP),
				NtfEmailRealTimeJob.class,
				SchedulerConstants.NTF_EMAIL_REAL_TIME_JOB_DESC, "0 0/5 * * * ?");
	}
}