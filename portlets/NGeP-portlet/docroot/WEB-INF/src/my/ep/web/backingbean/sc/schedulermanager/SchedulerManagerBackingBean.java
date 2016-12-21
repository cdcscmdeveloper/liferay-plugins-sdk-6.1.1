package my.ep.web.backingbean.sc.schedulermanager;

import java.util.ArrayList;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;

import my.ep.constant.MessageConst;
import my.ep.exception.EpException;
import my.ep.factory.EpLogger;
import my.ep.helper.EntityHelper;
import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.managedbean.sc.schedulermanager.SchedulerManagerManagedBean;
import my.ep.web.scheduler.constants.common.SchedulerConstants;
import my.ep.web.scheduler.helper.EpSchedulerHelper;
import my.ep.web.scheduler.job.LoadAllJob;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.ReflectionHelper;
import my.ep.web.util.sm.helper.SmCommonUtil;
import my.ep.web.vo.EpJobVO;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.primefaces.context.RequestContext;
import org.quartz.CronTrigger;
import org.quartz.Job;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.Trigger.TriggerState;
import org.quartz.impl.matchers.GroupMatcher;

import com.cronutils.descriptor.CronDescriptor;
import com.cronutils.model.CronType;
import com.cronutils.model.definition.CronDefinition;
import com.cronutils.model.definition.CronDefinitionBuilder;
import com.cronutils.parser.CronParser;

@ManagedBean(name = "schedulerManagerBB")
@RequestScoped
public class SchedulerManagerBackingBean extends
		BaseBackingBean<SchedulerManagerManagedBean> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1088034146543313228L;
	public static Logger log = EpLogger
			.getLogger(SchedulerManagerBackingBean.class);

	public void preRenderSchedulerManager() {
		renewSesionObject();
		SmCommonUtil.setMDCUserId();
		initAllJobs();
		initAllJobClasses();
		SmCommonUtil.redirectToPage("ePSchedulerManager.xhtml", "", null);
	}

	public void initAllJobs() {
		JobKey tmpJobKey = null;
		// loop jobs by group
		try {
			String jobAction = "";
			String jobState = "";
			Trigger trigger = null;
			EpJobVO jobVO = null;
			getManagedBean().setEpSchedulerJobList(new ArrayList<EpJobVO>());
			getManagedBean().setEpSchedulerProblemJobList(
					new ArrayList<EpJobVO>());
			getManagedBean().setJobGroups(
					EpSchedulerHelper.getScheduler().getJobGroupNames());

			for (String groupName : EpSchedulerHelper.getScheduler()
					.getJobGroupNames()) {

				// set search by group
				if (!StringUtils.isBlank(getManagedBean().getJobGroup())) {
					groupName = getManagedBean().getJobGroup();
				}

				// get jobkey
				for (JobKey jobKey : EpSchedulerHelper.getScheduler()
						.getJobKeys(GroupMatcher.jobGroupEquals(groupName))) {

					// set tmp jobkey to be deleted if got error
					tmpJobKey = jobKey;

					// get job details
					JobDetail job = EpSchedulerHelper.getScheduler()
							.getJobDetail(jobKey);

					// get job's trigger
					trigger = EpSchedulerHelper.getTriggerByJobKey(jobKey);

					if (job != null) {
						// get trigger state
						jobState = EpSchedulerHelper.getTriggerState(trigger);

						if (jobState.equals(TriggerState.PAUSED.toString())) {
							jobAction = SchedulerConstants.JOB_RESUME;
						} else {
							jobAction = SchedulerConstants.JOB_PAUSE;
						}
						jobVO = new EpJobVO(jobKey, trigger,
								getJobStatus(jobKey), jobAction, jobState,
								job.getDescription(), job.getJobClass(),
								EpSchedulerHelper
										.getCronExpressionByJobKey(jobKey));
						jobVO.setJobClassName(job.getJobClass().getName());
						jobVO.setJobClassSimpleName(job.getJobClass()
								.getSimpleName());
						jobVO.setJobGroup(jobKey.getGroup());
						jobVO.setJobName(jobKey.getName());
						jobVO.setCronExpressionDescription(constructExpression(jobVO
								.getCronExpression()));
						getManagedBean().getEpSchedulerJobList().add(jobVO);

						// construct problem jobs
						addProblemJobs(jobVO);

					}

				}

				// break loop if search by group
				if (!StringUtils.isBlank(getManagedBean().getJobGroup())) {
					break;
				}

			}

		} catch (SchedulerException e) {
			if (tmpJobKey != null) {
				log.error(">>>>>>ERROR INIT JOB:" + tmpJobKey.getName()
						+ " WITH EXCEPTION:" + e.getMessage() + "<<<<<<<<<");
			} else {
				log.error(">>>>>>ERROR INIT JOB EXCEPTION:" + e.getMessage()
						+ "<<<<<<<<<");
			}
		}
	}

	public JobKey getJobKey(String jobName, String searchGroupName) {
		JobKey returnJobKey = null;
		try {
			for (String groupName : EpSchedulerHelper.getScheduler()
					.getJobGroupNames()) {

				if (searchGroupName.equalsIgnoreCase(groupName)) {
					for (JobKey jobKey : EpSchedulerHelper.getScheduler()
							.getJobKeys(GroupMatcher.jobGroupEquals(groupName))) {

						if (jobKey.getName().equals(jobName)) {
							returnJobKey = jobKey;
							break;
						}
					}

					break;
				}

			}
		} catch (Exception e) {
			log.error(e);
		}

		return returnJobKey;

	}

	public void pauseOrResumeJob(EpJobVO epJobVO) {
		try {
			if (!EpSchedulerHelper.isJobRunningNow(epJobVO.getJobKey())) {
				if (epJobVO.getAction().equals(SchedulerConstants.JOB_PAUSE)) {
					EpSchedulerHelper.getScheduler().pauseJob(
							epJobVO.getJobKey());
				} else {
					EpSchedulerHelper.getScheduler().resumeJob(
							epJobVO.getJobKey());
				}
				initAllJobs();
			} else {
				throw new EpException(MessageConst.ERRQRTZ_00001);
			}
		} catch (SchedulerException e) {
			throw new EpException(MessageConst.ERRQRTZ_00003);
		}
	}

	public void runNowJob(EpJobVO epJobVO) {
		if (!EpSchedulerHelper.isJobRunningNow(epJobVO.getJobKey())) {
			boolean success = EpSchedulerHelper.runBuildJobNow(epJobVO
					.getJobKey());
			if (!success) {
				throw new EpException(MessageConst.ERRQRTZ_00003);
			} else {
				initAllJobs();
			}
		} else {
			throw new EpException(MessageConst.ERRQRTZ_00001);
		}
	}

	public void deleteJob() {
		boolean success = EpSchedulerHelper
				.deleteJobAndAssociatedTriggers(getManagedBean()
						.getSelectedJobVO().getJobKey());
		if (!success) {
			throw new EpException(MessageConst.ERRQRTZ_00002);
		} else {
			initAllJobs();
		}
	}

	public void recoverJob(EpJobVO epJobVO) {
		try {
			if (EpSchedulerHelper.getScheduler()
					.getTriggerState(epJobVO.getTrigger().getKey()).toString()
					.equals(TriggerState.BLOCKED.toString())) {
				JobDetail jobDetail = EpSchedulerHelper.getScheduler()
						.getJobDetail(epJobVO.getJobKey());
				if (jobDetail != null && jobDetail.getJobBuilder() != null) {
					if (jobDetail.requestsRecovery()) {
						initAllJobs();
					} else {
						rescheduledJob(epJobVO);
					}
				}
			}

		} catch (SchedulerException e) {
			throw new EpException(MessageConst.ERRQRTZ_00001);
		}
	}

	public void rescheduledJob(EpJobVO epJobVO) {
		try {
			if (EpSchedulerHelper.getScheduler()
					.getTriggerState(epJobVO.getTrigger().getKey()).toString()
					.equals(TriggerState.COMPLETE.toString())
					|| EpSchedulerHelper.getScheduler()
							.getTriggerState(epJobVO.getTrigger().getKey())
							.toString().equals(TriggerState.BLOCKED.toString())) {
				JobDetail jobDetail = EpSchedulerHelper.getScheduler()
						.getJobDetail(epJobVO.getJobKey());
				if (jobDetail != null && jobDetail.getJobBuilder() != null) {
					EpSchedulerHelper.getScheduler()
							.rescheduleJob(epJobVO.getTrigger().getKey(),
									epJobVO.getTrigger());
					initAllJobs();
				}
			}

		} catch (SchedulerException e) {
			throw new EpException(MessageConst.ERRQRTZ_00001);
		}
	}

	public void addOrUpdateJob() {
		try {
			getCronParser().parse(
					getManagedBean().getSelectedJobVO().getCronExpression());
		} catch (Exception ex) {
			throw new EpException(MessageConst.ERRQRTZ_00007);
		}
		if (getManagedBean().getSelectedJobVO().getJobKey() == null) {
			if (isJobAlreadyExist(getManagedBean().getSelectedJobVO()
					.getJobName())) {
				throw new EpException(MessageConst.ERRQRTZ_00006);
			}
			addJob();
		} else {
			updateJob();
		}
		getManagedBean().setJobGroup(
				getManagedBean().getSelectedJobVO().getJobGroup());
		initAllJobs();
		RequestContext.getCurrentInstance().execute("addJobDialog.hide()");
	}

	public void onAddJob() {
		getManagedBean().setOthersJobGroup(false);
		EpJobVO jobVO = new EpJobVO();
		getManagedBean().setSelectedJobVO(jobVO);
		RequestContext.getCurrentInstance().execute("addJobDialog.show()");
	}

	public void addJob() {
		Class<? extends Job> jobClass = getValidJobClass();

		try {
			EpSchedulerHelper.addAndScheduleNewJob(new JobKey(getManagedBean()
					.getSelectedJobVO().getJobName(), getManagedBean()
					.getSelectedJobVO().getJobGroup()), jobClass,
					getManagedBean().getSelectedJobVO().getDescription(),
					getManagedBean().getSelectedJobVO().getCronExpression(),
					null);
		} catch (Exception ex) {
			throw new EpException(MessageConst.ERRQRTZ_00004);
		}
	}

	public void onDeleteJob(EpJobVO epJobVO) {
		getManagedBean().setSelectedJobVO(epJobVO);
		RequestContext.getCurrentInstance().execute(
				"confirmDeleteDialog.show();");
	}

	public void onUpdateJob(EpJobVO epJobVO) {
		getManagedBean().setSelectedJobVO(
				(EpJobVO) EntityHelper.getEntityBySinglePropertyFromList(
						epJobVO.getJobName(), getManagedBean()
								.getEpSchedulerJobList(), "jobName"));
		RequestContext.getCurrentInstance().execute("addJobDialog.show()");
	}

	public void updateJob() {
		String finalCronExpression = getManagedBean().getSelectedJobVO()
				.getCronExpression();
		if (!getManagedBean()
				.getSelectedJobVO()
				.getJobName()
				.equals(getManagedBean().getSelectedJobVO().getJobKey()
						.getName().toString())) {
			if (isJobAlreadyExist(getManagedBean().getSelectedJobVO()
					.getJobName())) {
				throw new EpException(MessageConst.ERRQRTZ_00006);
			} else {
				EpSchedulerHelper
						.deleteJobAndAssociatedTriggers(getManagedBean()
								.getSelectedJobVO().getJobKey());
				addJob();
			}
		} else {
			if (!getManagedBean()
					.getSelectedJobVO()
					.getJobGroup()
					.equals(getManagedBean().getSelectedJobVO().getJobKey()
							.getGroup().toString())) {
				EpSchedulerHelper
						.deleteJobAndAssociatedTriggers(getManagedBean()
								.getSelectedJobVO().getJobKey());
				addJob();
			} else {
				if (!finalCronExpression.equals(getManagedBean()
						.getSelectedJobVO().getCronExpression())) {
					EpSchedulerHelper
							.deleteJobAndAssociatedTriggers(getManagedBean()
									.getSelectedJobVO().getJobKey());
					addJob();
				} else {
					JobKey jobKey = new JobKey(getManagedBean()
							.getSelectedJobVO().getJobName(), getManagedBean()
							.getSelectedJobVO().getJobGroup());
					EpSchedulerHelper.updateScheduledJob(jobKey,
							getManagedBean().getSelectedJobVO()
									.getDescription(), finalCronExpression,
							getValidJobClass());
				}
			}
		}
	}

	public String showMisFiredInstruction(int policy) {
		if (policy == CronTrigger.MISFIRE_INSTRUCTION_DO_NOTHING) {
			return "DO NOTHING";
		} else if (policy == CronTrigger.MISFIRE_INSTRUCTION_FIRE_ONCE_NOW) {
			return "FIRE ONCE NOW";
		} else if (policy == CronTrigger.MISFIRE_INSTRUCTION_IGNORE_MISFIRE_POLICY) {
			return "IGNORE MISFIRE POLICY";
		} else {
			return "SMART POLICY";
		}
	}

	public String constructExpression(String cronExpression) {
		String retVal = StringUtils.EMPTY;

		if (!StringUtils.isBlank(cronExpression)) {

			// create a parser based on provided definition
			CronParser parser = getCronParser();

			// create a descriptor for a specific Locale
			CronDescriptor descriptor = CronDescriptor
					.instance(new FacesScopes().getUserLocale());

			// parse some expression and ask descriptor for description
			retVal = "** " + descriptor.describe(parser.parse(cronExpression));
		}

		return retVal;
	}

	public void onBlurCronExpression() {
		getManagedBean().getSelectedJobVO().setCronExpressionDescription(
				constructExpression(getManagedBean().getSelectedJobVO()
						.getCronExpression()));
	}

	private String getJobStatus(JobKey jobKey) {
		String retVal = SchedulerConstants.JOB_WAIT;
		if (EpSchedulerHelper.isJobRunningNow(jobKey)) {
			retVal = SchedulerConstants.JOB_RUN;
		}
		return retVal;
	}

	private Class<? extends Job> getValidJobClass() {
		Class<? extends Job> jobClass;
		try {
			jobClass = (Class<? extends Job>) Class.forName(getManagedBean()
					.getSelectedJobVO().getJobClassName());
		} catch (ClassNotFoundException e) {
			throw new EpException(MessageConst.ERRQRTZ_00005);
		}
		return jobClass;
	}

	private boolean isJobAlreadyExist(String jobName) {
		boolean retVal = false;
		if (!EntityHelper.isCollectionEmpty(getManagedBean()
				.getEpSchedulerJobList())) {
			for (EpJobVO job : getManagedBean().getEpSchedulerJobList()) {
				if (!StringUtils.isBlank(jobName)
						&& jobName.equals(job.getJobKey().getName())) {
					retVal = true;
				}
			}
		}
		return retVal;
	}

	private void initAllJobClasses() {
		getManagedBean().setSelectedJobVO(new EpJobVO());
		getManagedBean().setJobClassList(new ArrayList<String>());

		Class<LoadAllJob> load = LoadAllJob.class;
		List<Class<?>> classList = ReflectionHelper.findClassesImpmenenting(
				Job.class, Package.getPackage("my.ep.web.scheduler.job"));
		for (Class<?> jobClass : classList) {
			try {
				if (!jobClass.equals(LoadAllJob.class)) {
					getManagedBean().getJobClassList().add(jobClass.getName());
				}
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
	}

	private void addProblemJobs(EpJobVO job) {
		if (getManagedBean().getEpSchedulerProblemJobList() != null) {
			if (job.getState() != null
					&& (job.getState().equals("BLOCKED") || job.getState()
							.equals("ERROR"))) {
				getManagedBean().getEpSchedulerProblemJobList().add(job);
			} else if (job.getTrigger() != null
					&& job.getTrigger().getPreviousFireTime() != null
					&& job.getTrigger().getNextFireTime() != null
					&& job.getTrigger().getNextFireTime()
							.before(job.getTrigger().getPreviousFireTime())) {
				getManagedBean().getEpSchedulerProblemJobList().add(job);
			}
		}
	}

	private CronParser getCronParser() {
		// get a predefined instance
		CronDefinition cronDefinition = CronDefinitionBuilder
				.instanceDefinitionFor(CronType.QUARTZ);

		// create a parser based on provided definition
		CronParser parser = new CronParser(cronDefinition);
		return parser;
	}

}
