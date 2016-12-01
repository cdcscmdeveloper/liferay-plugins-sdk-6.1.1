package my.ep.web.scheduler.job.gfmas;

import my.ep.biz.service.intg.gfmas.IGfmasServiceSO;
import my.ep.biz.service.pm.common.batch.IDIInterfaceSO;
import my.ep.biz.service.pm.common.interfaceptj.IInterfaceVotDanaSO;
import my.ep.biz.service.pm.common.interfaceptj.IInterfaceVotSO;
import my.ep.constant.IntegrationConst;
import my.ep.constant.ModuleCodeConst;
import my.ep.dto.pm.common.interfaceptj.InterfaceLogResDto;
import my.ep.dto.pm.common.interfaceptj.InterfaceVotDanaReqDTO;
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
public class GLDANAJob implements Job {

	public static Logger log = EpLogger.getLogger(GLDANAJob.class);

	public void execute(JobExecutionContext context)
			throws JobExecutionException {

		log.info("GLDANAJob start");
		IDIInterfaceSO interfaceSo = null;
		IInterfaceVotDanaSO interfaceDanaSo = null;
		try {
			interfaceSo = EpEjbBusinessDelegate.getInstance()
					.getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
							IDIInterfaceSO.class);
			
			interfaceDanaSo = EpEjbBusinessDelegate.getInstance()
					.getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
							IInterfaceVotDanaSO.class);
			InterfaceLogResDto resDTO = interfaceSo.retrieveInterfaceHeaderByServiceCode(IntegrationConst.GLDNA_SERVICE_CODE);
			
			InterfaceVotDanaReqDTO reqDTO = new InterfaceVotDanaReqDTO();
			
			if(resDTO.getListOfDiInterfaceLog()!=null && resDTO.getListOfDiInterfaceLog().size()>0){	
				DIInterfaceLog diLog = resDTO.getListOfDiInterfaceLog().get(0);
				if(diLog.getPmInterfaceDana()!=null && diLog.getPmInterfaceDana().size()>0){
					reqDTO.setInterfaceDanaLs(diLog.getPmInterfaceDana());
					reqDTO.setFileName(diLog.getFileName());
					reqDTO.setTransId(diLog.getTransId());
					interfaceDanaSo.saveOrUpdateVotDanaList(reqDTO);
				}
			}
			
		} catch (Exception e) {
			log.info("GLDANAJob :" + e.getMessage());
		}
		finally {
			interfaceSo = null;
		}
		log.info("GLDANAJob end");

	}

}
