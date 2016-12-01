package my.ep.web.scheduler.job.gfmas;

import my.ep.biz.service.pm.common.batch.IDIInterfaceSO;
import my.ep.biz.service.pm.common.interfaceptj.IInterfacePtjSO;
import my.ep.constant.IntegrationConst;
import my.ep.constant.ModuleCodeConst;
import my.ep.dto.pm.common.interfaceptj.InterfaceAgOffReqDTO;
import my.ep.dto.pm.common.interfaceptj.InterfaceLogResDto;
import my.ep.dto.pm.common.interfaceptj.InterfacePtjReqDTO;
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
public class GLPTJJob implements Job {

	public static Logger log = EpLogger.getLogger(GLPTJJob.class);

	public void execute(JobExecutionContext context)
			throws JobExecutionException {

		log.info("GLPTJJob start");
		IDIInterfaceSO interfaceSo = null;
		IInterfacePtjSO interfacePtjSo = null;
		try {
			interfaceSo = EpEjbBusinessDelegate.getInstance()
					.getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
							IDIInterfaceSO.class);
			
			interfacePtjSo = EpEjbBusinessDelegate.getInstance()
					.getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
							IInterfacePtjSO.class);
			InterfaceLogResDto resDTO = interfaceSo.retrieveInterfaceHeaderByServiceCode(IntegrationConst.GLPTJ_SERVICE_CODE);
			
			InterfacePtjReqDTO reqDTO = new InterfacePtjReqDTO();
			
			if(resDTO.getListOfDiInterfaceLog()!=null && resDTO.getListOfDiInterfaceLog().size()>0){	
				DIInterfaceLog diLog = resDTO.getListOfDiInterfaceLog().get(0);
				if(diLog.getPmInterfacePtj()!=null && diLog.getPmInterfacePtj().size()>0){
					reqDTO.setInterfacePtjLs(diLog.getPmInterfacePtj());
					reqDTO.setFileName(diLog.getFileName());
					reqDTO.setTransId(diLog.getTransId());
					interfacePtjSo.saveOrUpdateInterfacePtjList(reqDTO);
				}
			}
			
		} catch (Exception e) {
			log.info("GLPTJJob :" + e.getMessage());
		}
		finally {
			interfaceSo = null;
		}
		log.info("GLPTJJob end");

	}

}
