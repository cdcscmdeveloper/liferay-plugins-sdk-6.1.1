package my.ep.web.scheduler.job.sc;

import my.ep.biz.service.sc.quationtender.IQTProposalSO;
import my.ep.constant.ModuleCodeConst;
import my.ep.dto.sc.quationtender.ProposalReqDTO;
import my.ep.factory.EpLogger;
import my.ep.web.util.lookupservice.BusinessDelegate;

import org.apache.log4j.Logger;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

public class ProposalValidityExpiaryJob implements Job {
	private static Logger log = EpLogger
			.getLogger(ProposalValidityExpiaryJob.class);

	private void debugLogger(Object message) {
		if (log.isDebugEnabled()) {
			log.debug(message);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.quartz.Job#execute(org.quartz.JobExecutionContext)
	 */
	@Override
	public void execute(JobExecutionContext arg0) throws JobExecutionException {
		debugLogger("Start execute ");

		IQTProposalSO qtProposalSO;

		try {
			qtProposalSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.SUPPLIER_SELECTION, IQTProposalSO.class);
			ProposalReqDTO reqDTO = new ProposalReqDTO();
			qtProposalSO.proposalValidityExpiary(reqDTO);

		} finally {
			qtProposalSO = null;
		}
		debugLogger("End execute ");

	}

}
