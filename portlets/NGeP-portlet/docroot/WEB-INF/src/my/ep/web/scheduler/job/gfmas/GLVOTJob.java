package my.ep.web.scheduler.job.gfmas;

import my.ep.biz.service.intg.gfmas.IGfmasServiceSO;
import my.ep.biz.service.pm.common.batch.IDIInterfaceSO;
import my.ep.biz.service.pm.common.interfaceptj.IInterfaceVotSO;
import my.ep.constant.IntegrationConst;
import my.ep.constant.ModuleCodeConst;
import my.ep.dto.pm.common.interfaceptj.InterfaceLogResDto;
import my.ep.dto.pm.common.interfaceptj.InterfaceVotReqDTO;
import my.ep.factory.EpLogger;
import my.ep.persistence.entity.pm.common.DIInterfaceLog;
import my.ep.util.lookupservice.EpEjbBusinessDelegate;

import org.apache.log4j.Logger;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;


/*
 * @author hktan
 */

@DisallowConcurrentExecution
public class GLVOTJob implements Job {

	public static Logger log = EpLogger.getLogger(GLVOTJob.class);

	public void execute(JobExecutionContext context)
			throws JobExecutionException {

		log.info("GLVOTJob start");
		IDIInterfaceSO interfaceSo = null;
		IInterfaceVotSO interfaceVotSo = null;
		try {
			interfaceSo = EpEjbBusinessDelegate.getInstance()
					.getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
							IDIInterfaceSO.class);
			interfaceVotSo = EpEjbBusinessDelegate.getInstance()
					.getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
							IInterfaceVotSO.class);
			
			InterfaceLogResDto resDTO = interfaceSo.retrieveInterfaceHeaderByServiceCode(IntegrationConst.GLVOT_SERVICE_CODE);
			
			InterfaceVotReqDTO reqDTO = new InterfaceVotReqDTO();
			
			if(resDTO.getListOfDiInterfaceLog()!=null && resDTO.getListOfDiInterfaceLog().size()>0){	
				DIInterfaceLog diLog = resDTO.getListOfDiInterfaceLog().get(0);
				if(diLog.getPmInterfaceVot()!=null && diLog.getPmInterfaceVot().size()>0){
					reqDTO.setInterfaceVotLs(diLog.getPmInterfaceVot());
					reqDTO.setFileName(diLog.getFileName());
					reqDTO.setTransId(diLog.getTransId());
					interfaceVotSo.saveOrUpdateVotFundList(reqDTO);
				}
			}
			
		} catch (Exception e) {
			log.info("GLVOTJob :" + e.getMessage());
		}
		finally {
			interfaceSo = null;
		}
		log.info("GLVOTJob end");

	}

}
