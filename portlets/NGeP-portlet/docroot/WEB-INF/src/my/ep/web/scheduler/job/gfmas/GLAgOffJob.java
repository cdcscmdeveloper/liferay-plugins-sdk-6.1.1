package my.ep.web.scheduler.job.gfmas;

import my.ep.biz.service.intg.gfmas.IGfmasServiceSO;
import my.ep.biz.service.pm.common.batch.IDIInterfaceSO;
import my.ep.biz.service.pm.common.interfaceptj.IInterfaceAgOffSO;
import my.ep.biz.service.pm.common.interfaceptj.IInterfaceProjectSO;
import my.ep.biz.service.pm.common.interfaceptj.IInterfaceVotDanaSO;
import my.ep.biz.service.pm.common.interfaceptj.IInterfaceVotSO;
import my.ep.constant.IntegrationConst;
import my.ep.constant.ModuleCodeConst;
import my.ep.dto.pm.common.interfaceptj.InterfaceAgOffReqDTO;
import my.ep.dto.pm.common.interfaceptj.InterfaceLogResDto;
import my.ep.dto.pm.common.interfaceptj.InterfaceProjectReqDTO;
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
public class GLAgOffJob implements Job {

	public static Logger log = EpLogger.getLogger(GLAgOffJob.class);

	public void execute(JobExecutionContext context)
			throws JobExecutionException {

		log.info("GLAgOffJob start");
		IDIInterfaceSO interfaceSo = null;
		IInterfaceAgOffSO interfaceAgOffSo = null;
		try {
			interfaceSo = EpEjbBusinessDelegate.getInstance()
					.getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
							IDIInterfaceSO.class);
			
			interfaceAgOffSo = EpEjbBusinessDelegate.getInstance()
					.getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
							IInterfaceAgOffSO.class);
			InterfaceLogResDto resDTO = interfaceSo.retrieveInterfaceHeaderByServiceCode(IntegrationConst.GLBAC_SERVICE_CODE);
			
			InterfaceAgOffReqDTO reqDTO = new InterfaceAgOffReqDTO();
			
			if(resDTO.getListOfDiInterfaceLog()!=null && resDTO.getListOfDiInterfaceLog().size()>0){	
				DIInterfaceLog diLog = resDTO.getListOfDiInterfaceLog().get(0);
				if(diLog.getPmInterfaceAgOff()!=null && diLog.getPmInterfaceAgOff().size()>0){
					reqDTO.setInterfaceAgOffLs(diLog.getPmInterfaceAgOff());
					reqDTO.setFileName(diLog.getFileName());
					reqDTO.setTransId(diLog.getTransId());
					interfaceAgOffSo.saveOrUpdateInterfaceAgOffList(reqDTO);
				}
			}
			
		} catch (Exception e) {
			log.info("GLAgOffJob :" + e.getMessage());
		}
		finally {
			interfaceSo = null;
		}
		log.info("GLAgOffJob end");

	}

}
