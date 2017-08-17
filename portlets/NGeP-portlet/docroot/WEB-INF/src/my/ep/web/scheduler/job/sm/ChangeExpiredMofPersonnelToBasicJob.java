package my.ep.web.scheduler.job.sm;

import java.util.List;

import org.apache.log4j.Logger;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import my.ep.biz.service.sm.scheduler.ISmSchedulerSO;
import my.ep.dto.sm.scheduler.SmSchedulerReqDTO;
import my.ep.dto.sm.scheduler.SmSchedulerResDTO;
import my.ep.factory.EpLogger;
import my.ep.persistence.constant.DbValueConst;
import my.ep.persistence.entity.sm.SmMofAccount;
import my.ep.web.util.sm.helper.SmApplicationUtil;
import my.ep.web.util.sm.helper.SmServiceLookup;

public class ChangeExpiredMofPersonnelToBasicJob implements Job{
	public static Logger log = EpLogger.getLogger(ChangeExpiredMofPersonnelToBasicJob.class);

	@Override
	public void execute(JobExecutionContext arg0) throws JobExecutionException {
		ISmSchedulerSO reqSO = SmServiceLookup.getSmSchedulerSO();
		SmSchedulerResDTO resDTO = reqSO.retrieveExpiredMof();
		List<SmMofAccount> mofAccountLs = resDTO.getMofAccountLs();
		
		log.info("**********ChangeExpiredMofPersonnelToBasicJob - Start************");
		if(mofAccountLs.size() > 0){
			for(SmMofAccount mofAccount : mofAccountLs){
				log.info("Supplier ID >>>>" + mofAccount.getSmSupplier().getSupplierId());
				boolean isChangeStatus = false;
				boolean activeAppl = SmApplicationUtil.checkActiveSmApplForSupp(mofAccount.getSmSupplier());

				if(!activeAppl){
					isChangeStatus = true;
				}

				if(!isChangeStatus){
					boolean isHasActiveTransaction = SmApplicationUtil.hasActiveTransactions(mofAccount.getSmSupplier());
					if(!isHasActiveTransaction){
						isChangeStatus = true;
					}
				}
				
				//change role to basic
				if(isChangeStatus){
					//List<SmPersonnel> personnelLs = mofAccount.getSmSupplier().getSmAppl().getSmPersonnels();
					mofAccount.setRecordStatusFlg(DbValueConst.RECORD_STATUS_EXPIRED);
		
					SmSchedulerReqDTO schedulerReqDTO = new SmSchedulerReqDTO();
					schedulerReqDTO.setMofAccount(mofAccount);
					
					
					try{
						SmServiceLookup.getSmSchedulerSO().updateMOFSupplierPersonnelToBasic(schedulerReqDTO);
					}catch(Exception ex){
						log.info("ChangeExpiredMofPersonnelToBasicJob updateMoFAcc -  Exception>>>>" + 
								ex.getMessage() + "- supplier ID -" + mofAccount.getSmSupplier());
					}
				}
			}

		}
		
		log.info("**********ChangeExpiredMofPersonnelToBasicJob - End************");
	}
}