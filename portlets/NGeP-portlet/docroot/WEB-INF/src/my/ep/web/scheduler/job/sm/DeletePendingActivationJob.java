package my.ep.web.scheduler.job.sm;

import java.util.Date;

import my.ep.biz.service.sm.personnel.IPersonnelSO;
import my.ep.constant.ModuleCodeConst;
import my.ep.dto.sm.personnel.PersonnelReqDTO;
import my.ep.dto.sm.personnel.PersonnelResDTO;
import my.ep.factory.EpLogger;
import my.ep.web.util.DateUtils;
import my.ep.web.util.lookupservice.BusinessDelegate;

import org.apache.log4j.Logger;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

public class DeletePendingActivationJob implements Job {
	public static Logger log = EpLogger
			.getLogger(DeletePendingActivationJob.class);

	@Override
	public void execute(JobExecutionContext arg0) throws JobExecutionException {
		log.info("start executing delete pending job...");
		// TODO include business logic here
		Date activationDate = DateUtils.minusDays(new Date(), 7);
		PersonnelReqDTO reqDTO = new PersonnelReqDTO();
		reqDTO.setActivationDate(activationDate);
		IPersonnelSO reqSO = (IPersonnelSO) BusinessDelegate.getInstance()
				.getServiceObject(ModuleCodeConst.SUPPLIER_MANAGEMENT, IPersonnelSO.class);
		PersonnelResDTO resDTO = reqSO
				.deleteExpiredPendingActivationPersonnel(reqDTO);
		log.info("finish executing delete pending job with status:"+resDTO.isSuccessful()+"...");
	}

}
