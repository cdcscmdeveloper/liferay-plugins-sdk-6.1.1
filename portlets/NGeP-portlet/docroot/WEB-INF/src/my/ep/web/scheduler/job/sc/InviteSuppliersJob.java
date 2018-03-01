/**
 * 
 */
package my.ep.web.scheduler.job.sc;

import java.util.List;

import my.ep.biz.service.osb.sc.IQTOsbSO;
import my.ep.biz.service.sc.quationtender.IQTDetailSO;
import my.ep.constant.ModuleCodeConst;
import my.ep.dto.osb.sc.QTOsbReqDTO;
import my.ep.factory.EpLogger;
import my.ep.web.util.lookupservice.BusinessDelegate;

import org.apache.commons.collections.CollectionUtils;
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
			IQTDetailSO qtDetailSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.SUPPLIER_SELECTION, IQTDetailSO.class);
			IQTOsbSO qtOsbSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.SUPPLIER_SELECTION, IQTOsbSO.class);
			
			List<Long> list = qtDetailSO.retrievePublishedQTs();
			
			if (CollectionUtils.isNotEmpty(list)) {
				QTOsbReqDTO osbReqDTO = new QTOsbReqDTO();
				
				for (Long qtId : list) {
					try {
						osbReqDTO.setDocId(qtId);
						qtOsbSO.updateSupplierForPublishedQT(osbReqDTO);	
					} catch (Exception e) {
						log.error(e.getMessage(), e);
					} 
				}
			}
			
		} catch (Exception e) {
			log.error(e.getLocalizedMessage(), e);
			// TODO need to add error handling scenarios
		} finally {

		}
		debugLogger("End InviteSuppliersJob ");

	}



}
