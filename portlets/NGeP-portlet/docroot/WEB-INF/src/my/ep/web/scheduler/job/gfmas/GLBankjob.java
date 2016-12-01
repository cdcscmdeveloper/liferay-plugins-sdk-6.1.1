package my.ep.web.scheduler.job.gfmas;

import my.ep.biz.service.intg.gfmas.IGfmasServiceSO;
import my.ep.biz.service.pm.common.batch.IDIInterfaceSO;
import my.ep.biz.service.pm.common.interfaceptj.IInterfaceCmBankSO;
import my.ep.biz.service.pm.common.interfaceptj.IInterfaceGlAccSO;
import my.ep.biz.service.pm.common.interfaceptj.IInterfacePrgActSO;
import my.ep.biz.service.pm.common.interfaceptj.IInterfaceVotDanaSO;
import my.ep.biz.service.pm.common.interfaceptj.IInterfaceVotSO;
import my.ep.constant.IntegrationConst;
import my.ep.constant.ModuleCodeConst;
import my.ep.dto.pm.common.interfaceptj.InterfaceCmBankReqDTO;
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
public class GLBankjob implements Job {

	public static Logger log = EpLogger.getLogger(GLBankjob.class);

	public void execute(JobExecutionContext context)
			throws JobExecutionException {

		log.info("GLBankjob start");
		IDIInterfaceSO interfaceSo = null;
		IInterfaceCmBankSO interfaceGlBankSo = null;
		try {
			interfaceSo = EpEjbBusinessDelegate.getInstance()
					.getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
							IDIInterfaceSO.class);
			
			interfaceGlBankSo = EpEjbBusinessDelegate.getInstance()
					.getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
							IInterfaceCmBankSO.class);
			InterfaceLogResDto resDTO = interfaceSo.retrieveInterfaceHeaderByServiceCode(IntegrationConst.CMBNK_SERVICE_CODE);
			
			InterfaceCmBankReqDTO reqDTO = new InterfaceCmBankReqDTO();
			
			if(resDTO.getListOfDiInterfaceLog()!=null && resDTO.getListOfDiInterfaceLog().size()>0){	
				DIInterfaceLog diLog = resDTO.getListOfDiInterfaceLog().get(0);
				if(diLog.getPmInterfaceBank()!=null && diLog.getPmInterfaceBank().size()>0){
					reqDTO.setInterfaceBankLs(diLog.getPmInterfaceBank());
					reqDTO.setFileName(diLog.getFileName());
					reqDTO.setTransId(diLog.getTransId());
					interfaceGlBankSo.saveOrUpdateInterfaceBankList(reqDTO);
				}
			}
			
		} catch (Exception e) {
			log.info("GLBankjob :" + e.getMessage());
		}
		finally {
			interfaceSo = null;
		}
		log.info("GLBankjob end");

	}

}
