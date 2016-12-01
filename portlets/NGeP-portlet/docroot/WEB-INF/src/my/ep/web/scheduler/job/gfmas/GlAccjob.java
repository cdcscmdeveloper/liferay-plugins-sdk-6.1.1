package my.ep.web.scheduler.job.gfmas;

import java.util.HashMap;
import java.util.Map;

import my.ep.biz.service.intg.gfmas.IGfmasServiceSO;
import my.ep.biz.service.pm.common.batch.IDIInterfaceSO;
import my.ep.biz.service.pm.common.interfaceptj.IInterfaceGlAccSO;
import my.ep.biz.service.pm.common.interfaceptj.IInterfacePrgActSO;
import my.ep.biz.service.pm.common.interfaceptj.IInterfaceVotDanaSO;
import my.ep.biz.service.pm.common.interfaceptj.IInterfaceVotSO;
import my.ep.constant.IntegrationConst;
import my.ep.constant.ModuleCodeConst;
import my.ep.dto.pm.common.interfaceptj.InterfaceGlAccReqDTO;
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
public class GlAccjob implements Job {

	public static Logger log = EpLogger.getLogger(GlAccjob.class);

	public void execute(JobExecutionContext context)
			throws JobExecutionException {

		log.info("GlAccjob start");
		IDIInterfaceSO interfaceSo = null;
		IInterfaceGlAccSO interfaceGlAccSo = null;
		try {
			interfaceSo = EpEjbBusinessDelegate.getInstance()
					.getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
							IDIInterfaceSO.class);
			
			interfaceGlAccSo = EpEjbBusinessDelegate.getInstance()
					.getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
							IInterfaceGlAccSO.class);
			InterfaceLogResDto resDTO = interfaceSo.retrieveInterfaceHeaderByServiceCode(IntegrationConst.GLGLC_SERVICE_CODE);
			
			InterfaceGlAccReqDTO reqDTO = new InterfaceGlAccReqDTO();
			
			if(resDTO.getListOfDiInterfaceLog()!=null && resDTO.getListOfDiInterfaceLog().size()>0){	
				DIInterfaceLog diLog = resDTO.getListOfDiInterfaceLog().get(0);
				if(diLog.getPmInterfaceGlAccount()!=null && diLog.getPmInterfaceGlAccount().size()>0){
					reqDTO.setInterfaceGlAccLs(diLog.getPmInterfaceGlAccount());
					reqDTO.setFileName(diLog.getFileName());
					reqDTO.setTransId(diLog.getTransId());
					interfaceGlAccSo.saveOrUpdateGlAccList(reqDTO);
				}
			}
			
			
		} catch (Exception e) {
			log.info("GlAccjob :" + e.getMessage());
		}
		finally {
			interfaceSo = null;
		}
		log.info("GlAccjob end");

	}

}
