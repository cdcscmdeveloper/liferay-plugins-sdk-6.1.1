package my.ep.rest.sm;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

import my.ep.biz.service.ct.ICtPanelCategorySO;
import my.ep.biz.service.sc.quationtender.IQTProposalSO;
import my.ep.biz.service.sc.quationtender.IQTSpecSO;
import my.ep.biz.service.sm.scheduler.ISmSchedulerSO;
import my.ep.constant.ModuleCodeConst;
import my.ep.constant.sm.SupplierManagementConst;
import my.ep.dto.sc.quationtender.ProposalReqDTO;
import my.ep.dto.sm.scheduler.SmSchedulerReqDTO;
import my.ep.dto.sm.scheduler.SmSchedulerResDTO;
import my.ep.persistence.constant.DbValueConst;
import my.ep.persistence.entity.ct.circular.CtCircularVer;
import my.ep.persistence.entity.ct.circular.panel.CtPanelCategory;
import my.ep.persistence.entity.sc.quationtender.ScQt;
import my.ep.persistence.entity.sc.quationtender.ScQtCategory;
import my.ep.persistence.entity.sm.SmMofAccount;
import my.ep.util.lookupservice.EpEjbBusinessDelegate;
import my.ep.web.util.lookupservice.BusinessDelegate;
import my.ep.web.util.sm.helper.SmServiceLookup;

import org.apache.commons.collections.CollectionUtils;
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
	
	@GET
	@Path("/callMygpis/")
	public Response callMygpis() {
		log.info("Before success calling callMygpis rest api....loginId=");
 
		try {
			executeMygpisInfo();
 
			return Response.status(200).entity("success calling execute()!!").build();
		} catch (Exception ex) {
 
			return Response.status(406).entity(ex.getMessage()).build();
		}
	}
	public void executeMygpisInfo() {
		try {
			log.info("before close ejb for executeMygpisInfo");
 
			IQTProposalSO proposalSO = BusinessDelegate.getInstance()
					.getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION,
							IQTProposalSO.class);;

			ProposalReqDTO proposalReqDTO = new ProposalReqDTO();
			proposalReqDTO.setQtId(157023L);//set sample qt id
			ScQt qt = proposalSO.getQtByQtId(proposalReqDTO).getScQt();
			Long qtId = qt.getQtId();
//			insertQtCategoryCode(qt);

			
			log.info("$$$$$ qt Work Flow starting insertQtCategoryCode");
			CtCircularVer ctCircularVer = qt.getCircularVer() != null ? qt.getCircularVer() : null ;
			Long circularVerId;
			List<CtPanelCategory> ctPanelCategorycode = new ArrayList<CtPanelCategory>();
			ICtPanelCategorySO ctPanelCategorySO = EpEjbBusinessDelegate
					.getInstance().getServiceObject(
							ModuleCodeConst.CONTRACT_MANAGEMENT,
							ICtPanelCategorySO.class);
			
			boolean isCurrentCircular = null != ctCircularVer.getCtCircular()
					.getCurrentCircularVerId()
					&& null != ctCircularVer.getCtCircular()
							.getCurrentCircularVerId().getCircularVerId()
					&& !ctCircularVer.getCircularVerId().equals(
							ctCircularVer.getCtCircular()
									.getCurrentCircularVerId()
									.getCircularVerId());
			
			if (isCurrentCircular) {
				circularVerId = ctCircularVer.getCtCircular()
						.getCurrentCircularVerId().getCircularVerId();
				ctPanelCategorycode = ctPanelCategorySO
							.retrievePanelCategoryCodeByCircularVerId(circularVerId);
			} else {
				circularVerId = ctCircularVer.getCircularVerId();
				ctPanelCategorycode = ctPanelCategorySO
							.retrievePanelCategoryCodeByCircularVerId(circularVerId);
			}	
			
			if (CollectionUtils.isNotEmpty(ctPanelCategorycode)) {
				IQTSpecSO specSO = BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION,IQTSpecSO.class);
				List<ScQtCategory> scQtCategoryListNew = new ArrayList<>();
				for (CtPanelCategory panelCategoryCode : ctPanelCategorycode) {
					ScQtCategory scQtCategory = new ScQtCategory();
					scQtCategory.setCategoryType(String.valueOf(panelCategoryCode.getCategoryType().getParameterId()));
					scQtCategory.setPmCategoryL1(panelCategoryCode.getPmCategoryL1());
					scQtCategory.setPmCategoryL2(panelCategoryCode.getPmCategoryL2());
					scQtCategory.setPmCategoryL3(panelCategoryCode.getPmCategoryL3());
					scQtCategory.setPmCategoryL1Name(panelCategoryCode.getPmCategoryL1Name());
					scQtCategory.setPmCategoryL2Name(panelCategoryCode.getPmCategoryL2Name());
					scQtCategory.setPmCategoryL3Name(panelCategoryCode.getPmCategoryL3Name());
					scQtCategory.setMandatory(false);
					scQtCategory.setScQt(qt);
					scQtCategoryListNew.add(scQtCategory);
				}
					
				for (ScQtCategory scQtCategory : scQtCategoryListNew){
					specSO.insertCategoryCode(scQtCategory);
				}
			} 
			log.info("$$$$$ qt Work Flow end insertQtCategoryCode");
		
			//tmpSO.executeBulkSstPackage();
			//tmpSO.executeBulkPProcurementPackage();
			log.info("after close ejb for executeMygpisInfo");

 
		} catch (Exception ex) {
			//log.error(ex);
			ex.printStackTrace();
		} finally {
			// EjbRemoteHelper.getInstance().destroyContext(ctxMap);
			log.info("final close ejb for executeMygpisInfo");
		}
	}
	
	private void insertQtCategoryCode(ScQt scQt){		
		log.info("$$$$$ qt Work Flow starting insertQtCategoryCode");
		CtCircularVer ctCircularVer = scQt.getCircularVer() != null ? scQt.getCircularVer() : null ;
		Long circularVerId;
		List<CtPanelCategory> ctPanelCategorycode = new ArrayList<CtPanelCategory>();
		ICtPanelCategorySO ctPanelCategorySO = EpEjbBusinessDelegate
				.getInstance().getServiceObject(
						ModuleCodeConst.CONTRACT_MANAGEMENT,
						ICtPanelCategorySO.class);
		
		boolean isCurrentCircular = null != ctCircularVer.getCtCircular()
				.getCurrentCircularVerId()
				&& null != ctCircularVer.getCtCircular()
						.getCurrentCircularVerId().getCircularVerId()
				&& !ctCircularVer.getCircularVerId().equals(
						ctCircularVer.getCtCircular()
								.getCurrentCircularVerId()
								.getCircularVerId());
		
		if (isCurrentCircular) {
			circularVerId = ctCircularVer.getCtCircular()
					.getCurrentCircularVerId().getCircularVerId();
			ctPanelCategorycode = ctPanelCategorySO
						.retrievePanelCategoryCodeByCircularVerId(circularVerId);
		} else {
			circularVerId = ctCircularVer.getCircularVerId();
			ctPanelCategorycode = ctPanelCategorySO
						.retrievePanelCategoryCodeByCircularVerId(circularVerId);
		}	
		
		if (CollectionUtils.isNotEmpty(ctPanelCategorycode)) {
			IQTSpecSO specSO = BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION,IQTSpecSO.class);
			List<ScQtCategory> scQtCategoryListNew = new ArrayList<>();
			for (CtPanelCategory panelCategoryCode : ctPanelCategorycode) {
				ScQtCategory scQtCategory = new ScQtCategory();
				scQtCategory.setCategoryType(String.valueOf(panelCategoryCode.getCategoryType().getParameterId()));
				scQtCategory.setPmCategoryL1(panelCategoryCode.getPmCategoryL1());
				scQtCategory.setPmCategoryL2(panelCategoryCode.getPmCategoryL2());
				scQtCategory.setPmCategoryL3(panelCategoryCode.getPmCategoryL3());
				scQtCategory.setPmCategoryL1Name(panelCategoryCode.getPmCategoryL1Name());
				scQtCategory.setPmCategoryL2Name(panelCategoryCode.getPmCategoryL2Name());
				scQtCategory.setPmCategoryL3Name(panelCategoryCode.getPmCategoryL3Name());
				scQtCategory.setMandatory(false);
				scQtCategory.setScQt(scQt);
				scQtCategoryListNew.add(scQtCategory);
			}
				
			for (ScQtCategory scQtCategory : scQtCategoryListNew){
				specSO.insertCategoryCode(scQtCategory);
			}
		} 
		log.info("$$$$$ qt Work Flow end insertQtCategoryCode");
	}

}
