/**
 * 
 */
package my.ep.web.scheduler.job.sc;

import my.ep.biz.service.osb.sc.IQTOsbSO;
import my.ep.constant.ModuleCodeConst;
import my.ep.dto.osb.sc.QTOsbReqDTO;
import my.ep.factory.EpLogger;
import my.ep.web.util.lookupservice.BusinessDelegate;

import org.apache.log4j.Logger;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

/**
 * @author VidyaSagarReddy
 *
 * Class to insert supplier for published qt will be called every day..
 */

public class InviteSuppliersJob implements Job {

	private static Logger log = EpLogger
			.getLogger(InviteSuppliersJob.class);

	private void debugLogger(Object message) {
		if (log.isDebugEnabled()) {
			log.debug(message);
		}
	}
	
	/* (non-Javadoc)
	 * @see org.quartz.Job#execute(org.quartz.JobExecutionContext)
	 */
	@Override
	public void execute(JobExecutionContext arg0) throws JobExecutionException {
		debugLogger("Start InviteSuppliersJob ");
		try{
		IQTOsbSO  osbSO= BusinessDelegate.getInstance().getServiceObject(
				ModuleCodeConst.SUPPLIER_SELECTION, IQTOsbSO.class);
		QTOsbReqDTO osbReqDTO = new QTOsbReqDTO();
		 osbSO.updateSupplierForPublishedQT(osbReqDTO);
		}catch(Exception e){
			log.error(e.getLocalizedMessage());
			//TODO need to add error handling scenarios
		}finally{
			
		}
		debugLogger("End InviteSuppliersJob ");

	}



}
