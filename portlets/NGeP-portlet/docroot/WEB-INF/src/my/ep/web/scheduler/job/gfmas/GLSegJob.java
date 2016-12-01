package my.ep.web.scheduler.job.gfmas;

import my.ep.biz.service.intg.gfmas.IGfmasServiceSO;
import my.ep.biz.service.pm.common.batch.IDIInterfaceSO;
import my.ep.biz.service.pm.common.interfaceptj.IInterfaceKptjSO;
import my.ep.biz.service.pm.common.interfaceptj.IInterfacePpSO;
import my.ep.biz.service.pm.common.interfaceptj.IInterfaceProjectSO;
import my.ep.biz.service.pm.common.interfaceptj.IInterfaceVotDanaSO;
import my.ep.biz.service.pm.common.interfaceptj.IInterfaceVotSO;
import my.ep.constant.IntegrationConst;
import my.ep.constant.ModuleCodeConst;
import my.ep.dto.pm.common.interfaceptj.InterfaceKptjReqDTO;
import my.ep.dto.pm.common.interfaceptj.InterfaceLogResDto;
import my.ep.dto.pm.common.interfaceptj.InterfacePpReqDTO;
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
public class GLSegJob implements Job {

	public static Logger log = EpLogger.getLogger(GLSegJob.class);

	public void execute(JobExecutionContext context)
			throws JobExecutionException {

		log.info("GLSegJob start");
		IDIInterfaceSO interfaceSo = null;
		IInterfacePpSO interfacePpSo = null;
		try {
			interfaceSo = EpEjbBusinessDelegate.getInstance()
					.getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
							IDIInterfaceSO.class);
			
			interfacePpSo = EpEjbBusinessDelegate.getInstance()
					.getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
							IInterfacePpSO.class);
			InterfaceLogResDto resDTO = interfaceSo.retrieveInterfaceHeaderByServiceCode(IntegrationConst.GLSEG_SERVICE_CODE);
			
			InterfacePpReqDTO reqDTO = new InterfacePpReqDTO();
			
			if(resDTO.getListOfDiInterfaceLog()!=null && resDTO.getListOfDiInterfaceLog().size()>0){	
				DIInterfaceLog diLog = resDTO.getListOfDiInterfaceLog().get(0);
				if(diLog.getPmInterfacePP()!=null && diLog.getPmInterfacePP().size()>0){
					reqDTO.setInterfacePpLs(diLog.getPmInterfacePP());
					reqDTO.setFileName(diLog.getFileName());
					reqDTO.setTransId(diLog.getTransId());
					interfacePpSo.saveOrUpdateInterfacePpList(reqDTO);
				}
			}
			
		} catch (Exception e) {
			log.info("GLSegJob :" + e.getMessage());
		}
		finally {
			interfaceSo = null;
		}
		log.info("GLSegJob end");

	}

}
