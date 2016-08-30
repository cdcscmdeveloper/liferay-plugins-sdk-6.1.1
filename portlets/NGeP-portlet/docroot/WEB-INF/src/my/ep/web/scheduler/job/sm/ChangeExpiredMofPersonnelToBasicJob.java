package my.ep.web.scheduler.job.sm;

import java.util.List;

import my.ep.biz.service.sm.scheduler.ISmSchedulerSO;
import my.ep.dto.sm.mofAccount.MofAccountReqDTO;
import my.ep.dto.sm.personnel.PersonnelReqDTO;
import my.ep.dto.sm.scheduler.SmSchedulerResDTO;
import my.ep.factory.EpLogger;
import my.ep.persistence.constant.DbValueConst;
import my.ep.persistence.entity.sm.SmMofAccount;
import my.ep.persistence.entity.sm.SmPersonnel;
import my.ep.type.pm.RoleCode;
import my.ep.web.util.sm.helper.SmApplicationUtil;
import my.ep.web.util.sm.helper.SmServiceLookup;

import org.apache.log4j.Logger;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

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
					List<SmPersonnel> personnelLs = mofAccount.getSmSupplier().getSmAppl().getSmPersonnels();
					if(personnelLs.size() > 0){
						for(SmPersonnel personnel : personnelLs){
							if(personnel.getEpRole() != null){
								if(personnel.getEpRole().equals(RoleCode.MOF_SUPPLIER_ADMIN.value()) || 
										personnel.getEpRole().equals(RoleCode.MOF_SUPPLIER_USER.value())){
									String newEpRole = null;
									if(personnel.getEpRole().equals(RoleCode.MOF_SUPPLIER_ADMIN.value())){
										newEpRole = RoleCode.BASIC_SUPPLIER_ADMIN.value();
									}else{
										newEpRole = RoleCode.BASIC_SUPPLIER_USER.value();
									}
									PersonnelReqDTO personnelReqDTO = new PersonnelReqDTO();
									//personnelReqDTO.setSmPersonnel(personnel);
									personnelReqDTO.setPersonnelId(personnel.getPersonnelId());
									personnelReqDTO.setNewEpRole(newEpRole);

									log.info("Personnel ID >>>>" + personnel.getPersonnelId());
									log.info("Personnel NewEpRole >>>>" + newEpRole);
									log.info("Personnel OldEpRole >>>>" + personnel.getEpRole());
									try{
										SmServiceLookup.getPersonnelSO().updatePersonnelAndPmRole(personnelReqDTO);
									}catch(Exception ex){
										log.info("ChangeExpiredMofPersonnelToBasicJob Exception>>>>" + 
												ex.getMessage());
									}
								}
							}
						}
					}
					
					mofAccount.setRecordStatusFlg(DbValueConst.RECORD_STATUS_EXPIRED);
					MofAccountReqDTO mofAccountReqDTO = new MofAccountReqDTO();
					mofAccountReqDTO.setMofAccount(mofAccount);
					
					SmServiceLookup.getMofAccountSO().updateMofAccount(mofAccountReqDTO);
					
				}
			}

		}
		
		log.info("**********ChangeExpiredMofPersonnelToBasicJob - End************");
	}
}