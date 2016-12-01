package my.ep.web.scheduler.job.gfmas;

import my.ep.biz.service.intg.gfmas.IGfmasServiceSO;
import my.ep.biz.service.pm.common.batch.IDIInterfaceSO;
import my.ep.biz.service.pm.common.interfaceptj.IInterfacePrgActSO;
import my.ep.biz.service.pm.common.interfaceptj.IInterfaceVotDanaSO;
import my.ep.biz.service.pm.common.interfaceptj.IInterfaceVotSO;
import my.ep.constant.IntegrationConst;
import my.ep.constant.ModuleCodeConst;
import my.ep.dto.pm.common.interfaceptj.InterfaceLogResDto;
import my.ep.dto.pm.common.interfaceptj.InterfacePrgActReqDTO;
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
public class GLPrgActJob implements Job {

	public static Logger log = EpLogger.getLogger(GLPrgActJob.class);

	public void execute(JobExecutionContext context)
			throws JobExecutionException {

		log.info("GLPrgActJob start");
		IDIInterfaceSO interfaceSo = null;
		IInterfacePrgActSO interfaceprgActSo = null;
		try {
			interfaceSo = EpEjbBusinessDelegate.getInstance()
					.getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
							IDIInterfaceSO.class);
			
			interfaceprgActSo = EpEjbBusinessDelegate.getInstance()
					.getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
							IInterfacePrgActSO.class);
			InterfaceLogResDto resDTO = interfaceSo.retrieveInterfaceHeaderByServiceCode(IntegrationConst.GLPRG_SERVICE_CODE);
			
			InterfacePrgActReqDTO reqDTO = new InterfacePrgActReqDTO();
			
			if(resDTO.getListOfDiInterfaceLog()!=null && resDTO.getListOfDiInterfaceLog().size()>0){	
				DIInterfaceLog diLog = resDTO.getListOfDiInterfaceLog().get(0);
				if(diLog.getPmInterfaceProgAct()!=null && diLog.getPmInterfaceProgAct().size()>0){
					reqDTO.setInterfacePrgActLs(diLog.getPmInterfaceProgAct());
					reqDTO.setFileName(diLog.getFileName());
					reqDTO.setTransId(diLog.getTransId());
					interfaceprgActSo.saveOrUpdatePrgActList(reqDTO);
				}
			}
			
		} catch (Exception e) {
			log.info("GLPrgActJob :" + e.getMessage());
		}
		finally {
			interfaceSo = null;
		}
		log.info("GLPrgActJob end");

	}

}
