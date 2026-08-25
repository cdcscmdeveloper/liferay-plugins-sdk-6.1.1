package my.ep.rest.sm;

import java.io.IOException;
import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;

import my.ep.biz.service.osb.sm.ISMOsbSO;
import my.ep.constant.ModuleCodeConst;
import my.ep.dto.osb.sm.SMOsbReqDTO;
import my.ep.helper.EntityHelper;
import my.ep.persistence.entity.sm.SmAppl;
import my.ep.web.util.lookupservice.BusinessDelegate;
import my.ep.web.util.sm.helper.SmServiceLookup;
import org.apache.log4j.Logger;

/**
 * #28775 #39857
 * REST cancel pending appl
*/
@Path("/sm")
public class ApprovePendingCancelApplRest {

public static final Logger log = Logger.getLogger(ApprovePendingCancelApplRest.class);
	


	/**
	 * REST system auto-approve cancel pending appl, URL: cancelappl?no=xxx
	 *  
	 * @param applNo
	 * @return
	 * @throws IOException
	 * @return
	 */
	@GET
	@Path("/cancelappl")
	public String cancelAppl(@QueryParam("no") String applNo) throws IOException {
		log.info("Cancelling... " + applNo);
		SmServiceLookup.getReviewApplicationSO().cancelApplRest(applNo);
		return "Cancelled : "+applNo;
	}
	
	@GET
	@Path("/cancelapplall")
	public String cancelAll(){
		//SmServiceLookup.getReviewApplicationSO().cancelApplRest();
		log.info("Start Approve Cancel Application Menunggu Semakan Pembatalan");
		List<SmAppl> appls = SmServiceLookup.getReviewApplicationSO().getCancelApplRest();
		if (EntityHelper.isCollectionEmpty(appls)){
			return "No appl found to be Approve cancel";
		}
		log.info("Total Record to be Approve cancel ="+appls.size());
		for(SmAppl a : appls){
			try {
				log.info("Approve Cancel... " + a.getApplNo());
				SmServiceLookup.getReviewApplicationSO().cancelApplRest(a.getApplNo());
				log.info("Complete Approve Cancel... " + a.getApplNo());
			} catch (Exception e){
				log.error("Failed to Approve Cancel: " + a.getApplNo(), e);
			}
		}
		log.info("End Approve Cancel Application Menunggu Semakan Pembatalan");
		return "DONE";
	}
	
	/**
	 * REST system terminate/expired appl, URL: terminateappl?id=xxx
	 *  
	 * @param applNo
	 * @return
	 * @throws IOException
	 * @return
	 */
	@GET
	@Path("/terminateappl")
	public String terminate(@QueryParam("id") Long id) throws IOException {
		log.info("Terminating... " + id);
		// refer smapplbb.initiateSmApplicationTask
		
		boolean flag = SmServiceLookup.getReviewApplicationSO().terminateApplRest(id);
		if (flag){
			SMOsbReqDTO oreqDTO = new SMOsbReqDTO();
			oreqDTO.setApplicationId(id);
			ISMOsbSO osbSO = (ISMOsbSO) BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.SUPPLIER_MANAGEMENT,ISMOsbSO.class);
			osbSO.disableActiveSmappl(oreqDTO);
		}
		return "Done terminate : "+id;
	}
	
	@GET
	@Path("/terminateapplall")
	public String terminateAll(){
		log.info("Start Auto Reject Menunggu Maklum Balas Pertanyaan Oleh SDO & PO ");
		List<SmAppl> appls = SmServiceLookup.getReviewApplicationSO().terminateApplRest();
		if (EntityHelper.isCollectionEmpty(appls)){
			return "No appl found to be terminated";
		}
		log.info("Total Record to be Auto Reject ="+appls.size());
		for(SmAppl a : appls){
			try {
				log.info("Auto Reject... " + a.getApplNo());
				terminate(a.getApplId());
				log.info("Complete Auto Reject... " + a.getApplNo());
			} catch (Exception e){
				log.error("Failed to Auto Reject: " + a.getApplNo(), e);
			}
		}
		log.info("Tamat Auto Reject Menunggu Maklum Balas Pertanyaan Oleh SDO & PO ");
		return "DONE";
	}
}
