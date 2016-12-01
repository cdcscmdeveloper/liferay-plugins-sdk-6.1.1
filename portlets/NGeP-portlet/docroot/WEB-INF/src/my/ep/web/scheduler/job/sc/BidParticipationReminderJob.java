package my.ep.web.scheduler.job.sc;

import java.util.Date;
import java.util.List;

import my.ep.web.util.DateUtils;

import org.apache.commons.collections.CollectionUtils;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

/**
 * Reminder to supplier to participate in Bidding 
 * @author Venkat Chanumolu
 * @since 28/11/2016
 *
 */
@DisallowConcurrentExecution
public class BidParticipationReminderJob extends BaseReminderJob{

	/**
	 * Generated Serial Version Id
	 */
	private static final long serialVersionUID = 773402779426864100L;

	@Override
	public void execute(JobExecutionContext context)
			throws JobExecutionException {
		List<ReminderBean> beans = getReminderBeans();
		// no.of reminder days
		List<Integer> notificationDays =  getNotificationDays(getKpiCode());

		for (ReminderBean reminderBean : beans) {
			Long reminderDays = DateUtils.getDaysDifference(reminderBean.getDueDate(), new Date());

			if(CollectionUtils.isNotEmpty(notificationDays) && notificationDays.contains(reminderDays)){
			//	List<String> paramList = getParams(reminderDays, reminderBean.getData());

				// send reminder
				//sendReminder(super.getTemplateId(), reminderBean,  paramList);
			}
		}
	}
	
	protected List<String> getParameterListForNotification(Long reminderDays,Object[] data){
		return null;
	}

	@Override
	public String getTemplateId() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getKpiCode() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<String> getParams(Long reminderDays, Object[] data) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<ReminderBean> getReminderBeans() {
		// TODO Auto-generated method stub
		return null;
	}
}
