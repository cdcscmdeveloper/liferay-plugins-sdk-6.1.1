package my.ep.web.scheduler.job.sc;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import my.ep.biz.service.sc.bidding.IBiddingSO;
import my.ep.constant.ModuleCodeConst;
import my.ep.constant.NotificationTextConst;
import my.ep.dto.sc.bidding.BiddingResDTO;
import my.ep.factory.EpLogger;
import my.ep.persistence.entity.sc.bidding.ScBidSchedule;
import my.ep.web.constant.sc.QTConstants;
import my.ep.web.util.lookupservice.BusinessDelegate;

import org.apache.log4j.Logger;
import org.quartz.DisallowConcurrentExecution;

/**
 * Reminder to supplier to participate in Bidding
 * 
 * @author Venkat Chanumolu
 * @since 28/11/2016
 * 
 */
@DisallowConcurrentExecution
public class BidParticipationReminderJob extends BaseReminderJob {

	private static Logger log = EpLogger
			.getLogger(BidParticipationReminderJob.class);

	/**
	 * Generated Serial Version Id
	 */
	private static final long serialVersionUID = 773402779426864100L;

	@Override
	public String getTemplateId() {
		// TODO Auto-generated method stub
		return NotificationTextConst.TEMPLATE_ID_401165;
	}

	@Override
	public String getKpiCode() {
		return QTConstants.KPI_CODE_BIDDING_REMINDER;
	}

	@Override
	public List<String> getParams(Long reminders, Object[] data) {
		String[] paramData = (String[])data;
		String ministryName = paramData[0].toString();
		String ptjCode = paramData[1].toString();
		String ptjName = paramData[2].toString();
		String qtNo = paramData[3].toString();
		String qtTitle = paramData[4].toString();
		String publishDate = paramData[5].toString();
		String status = paramData[6].toString();
		

		List<String> paramList = new ArrayList<String>();

		// 1. Ministry Name
		paramList.add(ministryName);

		// 2. PTJ Code
		paramList.add(ptjCode);

		// 3. PTJ Name
		paramList.add(ptjName);

		// 4. qtNo
		paramList.add(qtNo);

		// 5. Qt Title 
		paramList.add(qtTitle);

		// 6. Publish Date and Time
		paramList.add(publishDate);

		// 7. Status
		paramList.add(status);
		return paramList;
	}

	@Override
	public List<ReminderBean> getReminderBeans() {
		IBiddingSO biddingSO = null;
		List<ReminderBean> beans = null;
		try {
			biddingSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.SUPPLIER_SELECTION, IBiddingSO.class);
			BiddingResDTO resDTO = biddingSO.getJobBidSchedulesForRemnder();
			if (resDTO.isSuccessful()) {
				beans = new ArrayList<ReminderBean>();
				Map<Long, String[]> bidWiseUserIds = resDTO
						.getBidWiseSupplierUserIds();
				Map<Long, String[]> bidWiseReminderData = resDTO
						.getBidWiseReminderData();

				for (ScBidSchedule nextDayBid : resDTO.getActiveBidSchedules()) {
					ReminderBean reminderBean = new ReminderBean();
					reminderBean.setDueDate(nextDayBid.getStartDate());
					List<String> receipients = new ArrayList<String>(
							Arrays.asList(bidWiseUserIds.get(nextDayBid
									.getBidScheduleId())));
					reminderBean.setReciepts(receipients);
					reminderBean.setDocNo(nextDayBid.getScBid().getDocNo());
					reminderBean.setData(bidWiseReminderData.get(nextDayBid
							.getBidScheduleId()));
				}

			}

		} catch (Exception e) {
			log.info(e.getLocalizedMessage());
		}

		return beans;
	}

	@Override
	public List<String> getSubjectParams(Object[] data) {
		String[] paramData = (String[])data;
		String bidStartDate = paramData[7].toString();
		List<String> paramList = new ArrayList<String>();
		paramList.add(bidStartDate);
		return paramList;
	}
}
