package my.ep.rest.sm;

import java.util.Date;
import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

import org.apache.log4j.Logger;

import my.ep.biz.service.sm.scheduler.ISmSchedulerSO;
import my.ep.dto.sm.scheduler.SmSchedulerReqDTO;
import my.ep.dto.sm.scheduler.SmSchedulerResDTO;
import my.ep.persistence.constant.DbValueConst;
import my.ep.persistence.entity.sm.SmMofAccount;
import my.ep.persistence.entity.sm.SmPersonnel;
import my.ep.persistence.entity.sm.SmSupplier;
import my.ep.web.util.sm.helper.SmServiceLookup;

@Path("/sm")
public class DowngradeExpMofPersonnelRest {

	public static Logger log = Logger.getLogger(DowngradeExpMofPersonnelRest.class);

	@GET
	@Path("downgradeexpiredpersonnel")
	public Response downgradeExpMofPersonnel() {
		log.info("Before success calling downgradeExpMofPersonnel rest api....");
		try {
			downgradeMOFToBasic();
			changeExpiredMofPersonnelToDeleted();
			//33628 START
			updateForMOFExpiredAndActiveSupplier();
			updateForMOFDeletedAndSuspendSupplier();
			//33628 END
			return Response.status(200).entity("success calling downgradeExpMofPersonnel!!").build();
		} catch (Exception ex) {
			return Response.status(406).entity(ex.getMessage()).build();
		}
	}
	
	private void downgradeMOFToBasic(){
		ISmSchedulerSO reqSO = SmServiceLookup.getSmSchedulerSO();
		SmSchedulerResDTO resDTO = reqSO.retrieveExpiredMof();
		List<SmMofAccount> mofAccountLs = resDTO.getMofAccountLs();

		log.info("**********downgradeMOFToBasic - Start************");
		if (mofAccountLs.size() > 0) {
			for (SmMofAccount mofAccount : mofAccountLs) {
				if (mofAccount.getSmSupplier().getSmAppl() != null) {
					mofAccount.setRecordStatusFlg(DbValueConst.RECORD_STATUS_EXPIRED);
					mofAccount.setLastCheckDate(new Date());
					SmSchedulerReqDTO schedulerReqDTO = new SmSchedulerReqDTO();
					schedulerReqDTO.setMofAccount(mofAccount);

					try {
						reqSO.updateMOFSupplierPersonnelToBasic(schedulerReqDTO);
					} catch (Exception ex) {
						log.info("ChangeExpiredMofPersonnelToBasicJob updateMoFAcc -  Exception>>>>"
								+ ex.getMessage() + "- supplier ID -" + mofAccount.getSmSupplier().getSupplierId());
					}
				}
			}
			log.info("**********downgradeMOFToBasic - END************");
		}
	}

	private void changeExpiredMofPersonnelToDeleted() {
		ISmSchedulerSO reqSO = SmServiceLookup.getSmSchedulerSO();
		SmSchedulerResDTO resDTO = reqSO.retrieveExpiredMofPendingDeletion();
		List<SmMofAccount> mofAccountLs = resDTO.getMofAccountLs();
		log.info("**********changeExpiredMofPersonnelToDeleted - Start************");
		for (SmMofAccount mofAccount : mofAccountLs) {
			mofAccount.setRecordStatusFlg(DbValueConst.RECORD_STATUS_DELETED);
			mofAccount.setLastCheckDate(new Date());
			SmSchedulerReqDTO schedulerReqDTO = new SmSchedulerReqDTO();
			schedulerReqDTO.setMofAccount(mofAccount);

			try {
				reqSO.updateExpiredMOFSupplierPersonnel(schedulerReqDTO);
			} catch (Exception ex) {
				log.info("ChangeExpiredMofPersonnelToBasicJob updateMoFAcc -  Exception>>>>" + ex.getMessage()
						+ "- supplier ID -" + mofAccount.getSmSupplier().getSupplierId());
			}
			
			// #28800 START
			try {
				reqSO.sendEmailExpiredMOFToSupplier(schedulerReqDTO);
			} catch (Exception ex){
				log.info("sendEmailExpiredMOFToSupplier ERROR :: " +ex.getMessage());
			}
			// #28800 END
		}
		log.info("**********changeExpiredMofPersonnelToDeleted - END************");
	}
	//33628 START
	private void updateForMOFExpiredAndActiveSupplier() {
		ISmSchedulerSO reqSO = SmServiceLookup.getSmSchedulerSO();
		SmSchedulerResDTO resDTO = reqSO.retrieveMOFExpiredAndActiveSupp();
		List<SmSupplier> suppLs = resDTO.getSmSupplierList();
		log.info("**********updateSupplierForMOFExpired - Start************");
		if(suppLs.size() > 0){
			for(SmSupplier smSupp : suppLs){
				SmSchedulerReqDTO schedulerReqDTO = new SmSchedulerReqDTO();
				schedulerReqDTO.setSmSupplier(smSupp);
				if(schedulerReqDTO.getSmSupplier() != null){
					try {
						reqSO.updateSupplierForMOFExpiredNotLogin1Y(schedulerReqDTO);
					} catch (Exception ex) {
						log.info("updateSupplierForMOFExpired -  Exception>>>>" + ex.getMessage());
					}
				}
			}
		}
		log.info("**********updateSupplierForMOFExpired - END************");
	}
		
	private void updateForMOFDeletedAndSuspendSupplier() {
		ISmSchedulerSO reqSO = SmServiceLookup.getSmSchedulerSO();
		SmSchedulerResDTO resDTO = reqSO.retrieveMOFDeletedAndSuspendSupp();
		List<SmSupplier> suppLs = resDTO.getSmSupplierList();
		log.info("**********updateSupplierForMOFDeleted - Start************");
		if(suppLs.size() > 0){
			for(SmSupplier smSupp : suppLs){
				SmSchedulerReqDTO schedulerReqDTO = new SmSchedulerReqDTO();
				schedulerReqDTO.setSmSupplier(smSupp);
				if(schedulerReqDTO.getSmSupplier() != null){
					try {
						reqSO.updateSupplierForMOFDeletedNotLogin2Y(schedulerReqDTO);
					} catch (Exception ex) {
						log.info("updateSupplierForMOFDeleted -  Exception>>>>" + ex.getMessage());
					}
				}
			}
		}
		log.info("**********updateSupplierForMOFDeleted - END************");
	}
	//33628 END
	
}
