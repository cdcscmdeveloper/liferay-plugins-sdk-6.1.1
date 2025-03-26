package my.ep.rest.sm;

import java.util.Date;
import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

import my.ep.biz.service.sm.scheduler.ISmSchedulerSO;
import my.ep.constant.sm.SupplierManagementConst;
import my.ep.dto.sm.scheduler.SmSchedulerReqDTO;
import my.ep.dto.sm.scheduler.SmSchedulerResDTO;
import my.ep.persistence.constant.DbValueConst;
import my.ep.persistence.entity.sm.SmMofAccount;
import my.ep.web.util.sm.helper.SmServiceLookup;

import org.apache.log4j.Logger;

@Path("/sm")
public class DowngradeExpMofPersonnelRest {

	public static Logger log = Logger.getLogger(DowngradeExpMofPersonnelRest.class);

	@GET
	@Path("downgradeexpiredpersonnel")
	public Response downgradeExpMofPersonnel() {
		log.info("Before success calling downgradeExpMofPersonnel rest api....");
		try {
			//33628 START
			downgradeMOFToBasic();
			changeExpiredMofPersonnelToDeleted();
			updateMOFExpiredMoreThanTwoYears();
			updateForMOFExpiredMoreThanThreeYears();
			//33628 END
			return Response.status(200).entity("success calling downgradeExpMofPersonnel!!").build();
		} catch (Exception ex) {
			return Response.status(406).entity(ex.getMessage()).build();
		}
	}
	
	//33628 START
	private void downgradeMOFToBasic(){
		ISmSchedulerSO reqSO = SmServiceLookup.getSmSchedulerSO();
		SmSchedulerResDTO resDTO = reqSO.retrieveExpiredMof();
		List<SmMofAccount> mofAccountLs = resDTO.getMofAccountLs();

		log.info("**********downgradeMOFToBasic - Start************");
		if (!mofAccountLs.isEmpty()) {
			for (SmMofAccount mofAccount : mofAccountLs) {
				if (mofAccount.getSmSupplier().getSmAppl() != null) {
					mofAccount.setPrevMofStatus(mofAccount.getRecordStatusFlg());
					mofAccount.setRecordStatusFlg(DbValueConst.RECORD_STATUS_EXPIRED);
					mofAccount.setLastCheckDate(new Date());
					mofAccount.setChangedBy(null);
					
					SmSchedulerReqDTO schedulerReqDTO = new SmSchedulerReqDTO();
					mofAccount.setNewMofStatus(mofAccount.getRecordStatusFlg());
					schedulerReqDTO.setMofAccount(mofAccount);
					schedulerReqDTO.setSchedulerType(SupplierManagementConst.SM_SCHEDULER_MOF_EXPIRED_DOWNGRADE_BASIC);
					schedulerReqDTO.setSmSupplier(mofAccount.getSmSupplier());
					schedulerReqDTO.getSmSupplier().setSmAppl(mofAccount.getSmSupplier().getSmAppl());
					schedulerReqDTO.getSmSupplier().setPrevSuppStatus(mofAccount.getSmSupplier().getRecordStatusFlg());

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
		if (!mofAccountLs.isEmpty()) {
			for (SmMofAccount mofAccount : mofAccountLs) {
				if(mofAccount.getSmSupplier().getSmAppl() != null){
					mofAccount.setPrevMofStatus(mofAccount.getRecordStatusFlg());
					mofAccount.setRecordStatusFlg(DbValueConst.RECORD_STATUS_DELETED);
					mofAccount.setLastCheckDate(new Date());
					mofAccount.setChangedBy(null);
					
					SmSchedulerReqDTO schedulerReqDTO = new SmSchedulerReqDTO();
					mofAccount.setNewMofStatus(mofAccount.getRecordStatusFlg());
					schedulerReqDTO.setMofAccount(mofAccount);
					schedulerReqDTO.setSchedulerType(SupplierManagementConst.SM_SCHEDULER_MOF_EXPIRED_ONEYEAR);
					schedulerReqDTO.setSmSupplier(mofAccount.getSmSupplier());
					schedulerReqDTO.getSmSupplier().setSmAppl(mofAccount.getSmSupplier().getSmAppl());

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
			}
		}
		log.info("**********changeExpiredMofPersonnelToDeleted - END************");
	}
	
	private void updateMOFExpiredMoreThanTwoYears() {
		ISmSchedulerSO reqSO = SmServiceLookup.getSmSchedulerSO();
		SmSchedulerResDTO resDTO = reqSO.retrieveMOFExpiredMoreThanTwoYears();
		List<SmMofAccount> mofAccountLs = resDTO.getMofAccountLs();
		log.info("**********updateSupplierForMOFExpired - Start************");
		if (!mofAccountLs.isEmpty()) {
			for (SmMofAccount mofAccount : mofAccountLs) {
				if (mofAccount.getSmSupplier().getSmAppl() != null) {
					mofAccount.setPrevMofStatus(mofAccount.getRecordStatusFlg());
					mofAccount.setRecordStatusFlg(DbValueConst.RECORD_STATUS_DELETED);
					mofAccount.setLastCheckDate(new Date());
					mofAccount.setChangedBy(null);
					
					SmSchedulerReqDTO schedulerReqDTO = new SmSchedulerReqDTO();
					mofAccount.setNewMofStatus(mofAccount.getRecordStatusFlg());
					schedulerReqDTO.setMofAccount(mofAccount);
					schedulerReqDTO.setSchedulerType(SupplierManagementConst.SM_SCHEDULER_MOF_EXPIRED_TWOYEARS);
					schedulerReqDTO.setSmSupplier(mofAccount.getSmSupplier());
					schedulerReqDTO.getSmSupplier().setPrevSuppStatus(mofAccount.getSmSupplier().getRecordStatusFlg());
					try {
						reqSO.updateMOFExpiredMoreThanTwoYears(schedulerReqDTO);
					} catch (Exception ex) {
						log.info("updateMOFExpiredMoreThanTwoYears -  Exception>>>>"
								+ ex.getMessage() + "- supplier ID -" + mofAccount.getSmSupplier().getSupplierId());
					}
				}
			}
		}
		log.info("**********updateMOFExpiredMoreThanTwoYears - END************");
	}
		
	private void updateForMOFExpiredMoreThanThreeYears() {
		ISmSchedulerSO reqSO = SmServiceLookup.getSmSchedulerSO();
		SmSchedulerResDTO resDTO = reqSO.retrieveMOFExpiredMoreThanThreeYears();
		List<SmMofAccount> mofAccountLs = resDTO.getMofAccountLs();
		log.info("**********updateForMOFExpiredMoreThanThreeYears - Start************");
		if (!mofAccountLs.isEmpty()) {
			for (SmMofAccount mofAccount : mofAccountLs) {
				if (mofAccount.getSmSupplier().getSmAppl() != null) {
					mofAccount.setPrevMofStatus(mofAccount.getRecordStatusFlg());
					mofAccount.setRecordStatusFlg(DbValueConst.RECORD_STATUS_DELETED);
					mofAccount.setLastCheckDate(new Date());
					mofAccount.setChangedBy(null);
					
					SmSchedulerReqDTO schedulerReqDTO = new SmSchedulerReqDTO();
					mofAccount.setNewMofStatus(mofAccount.getRecordStatusFlg());
					schedulerReqDTO.setMofAccount(mofAccount);
					schedulerReqDTO.setSchedulerType(SupplierManagementConst.SM_SCHEDULER_MOF_EXPIRED_THREEYEARS);
					schedulerReqDTO.setSmSupplier(mofAccount.getSmSupplier());
					schedulerReqDTO.getSmSupplier().setPrevSuppStatus(mofAccount.getSmSupplier().getRecordStatusFlg());
					try {
						reqSO.updateForMOFExpiredMoreThanThreeYears(schedulerReqDTO);
					} catch (Exception ex) {
						log.info("updateForMOFExpiredMoreThanThreeYears -  Exception>>>>"
								+ ex.getMessage() + "- supplier ID -" + mofAccount.getSmSupplier().getSupplierId());
					}
				}
			}
		}
		log.info("**********updateForMOFExpiredMoreThanThreeYears - END************");
	}
	//33628 END
	
}
