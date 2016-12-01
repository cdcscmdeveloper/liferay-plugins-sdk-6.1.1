package my.ep.web.scheduler.job.gfmas;

import my.ep.biz.service.intg.gfmas.IGfmasServiceSO;
import my.ep.biz.service.pm.common.batch.IDIInterfaceSO;
import my.ep.biz.service.pm.common.interfaceptj.IInterfaceKptjSO;
import my.ep.biz.service.pm.common.interfaceptj.IInterfaceProjectSO;
import my.ep.biz.service.pm.common.interfaceptj.IInterfaceVotDanaSO;
import my.ep.biz.service.pm.common.interfaceptj.IInterfaceVotSO;
import my.ep.constant.IntegrationConst;
import my.ep.constant.ModuleCodeConst;
import my.ep.dto.pm.common.interfaceptj.InterfaceKptjReqDTO;
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
public class GLKPTJJob implements Job {

	public static Logger log = EpLogger.getLogger(GLKPTJJob.class);

	public void execute(JobExecutionContext context)
			throws JobExecutionException {

		log.info("GLKPTJJob start");
		IDIInterfaceSO interfaceSo = null;
		IInterfaceKptjSO interfaceKptjSo = null;
		try {
			interfaceSo = EpEjbBusinessDelegate.getInstance()
					.getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
							IDIInterfaceSO.class);
			
			interfaceKptjSo = EpEjbBusinessDelegate.getInstance()
					.getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
							IInterfaceKptjSO.class);
			InterfaceLogResDto resDTO = interfaceSo.retrieveInterfaceHeaderByServiceCode(IntegrationConst.GLPCG_SERVICE_CODE);
			
			InterfaceKptjReqDTO reqDTO = new InterfaceKptjReqDTO();
			
			if(resDTO.getListOfDiInterfaceLog()!=null && resDTO.getListOfDiInterfaceLog().size()>0){	
				DIInterfaceLog diLog = resDTO.getListOfDiInterfaceLog().get(0);
				if(diLog.getPmInterfaceKptj()!=null && diLog.getPmInterfaceKptj().size()>0){
					reqDTO.setInterfaceKptjLs(diLog.getPmInterfaceKptj());
					reqDTO.setFileName(diLog.getFileName());
					reqDTO.setTransId(diLog.getTransId());
					interfaceKptjSo.saveOrUpdateInterfaceKptjList(reqDTO);
				}
			}
			
		} catch (Exception e) {
			log.info("GLKPTJJob :" + e.getMessage());
		}
		finally {
			interfaceSo = null;
		}
		log.info("GLKPTJJob end");

	}

}
