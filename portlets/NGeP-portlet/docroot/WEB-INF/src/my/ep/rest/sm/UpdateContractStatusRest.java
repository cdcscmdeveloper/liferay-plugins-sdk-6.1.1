package my.ep.rest.sm;

import java.io.IOException;

import javax.ws.rs.GET;
import javax.ws.rs.Path;

import my.ep.biz.service.sm.compexp.ISmCompExpSO;
import my.ep.constant.ModuleCodeConst;
import my.ep.web.util.lookupservice.BusinessDelegate;

import org.apache.log4j.Logger;

/**
 * REST endpoint for updating contract status base on Contract Effective Date or/and Contract Expiry Date 
 * #9299
*/
@Path("/sm")
public class UpdateContractStatusRest {

public static final Logger log = Logger.getLogger(UpdateContractStatusRest.class);

	/**
	 * REST endpoint to trigger a update of the contract status.
	 * @return
	 */
	@GET
	@Path("/update-contract-status")
	public String updatecontractstatus() throws IOException {
		log.info("*** Starting REST : updatecontractstatus");
		ISmCompExpSO service = BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.SUPPLIER_MANAGEMENT, ISmCompExpSO.class);
		service.updateContractStatuses();
		return "Done updatecontractstatus!";
	}
	
}
