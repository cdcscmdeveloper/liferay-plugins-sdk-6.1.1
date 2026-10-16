package my.ep.rest.sm;

import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import my.ep.biz.service.sm.scheduler.ISmSchedulerSO;
import my.ep.constant.sm.SupplierManagementConst;
import my.ep.dto.sm.scheduler.SmSchedulerReqDTO;
import my.ep.dto.sm.scheduler.SmSchedulerResDTO;
import my.ep.persistence.entity.sm.SmSupplier;
import my.ep.web.util.sm.helper.SmServiceLookup;

import org.apache.log4j.Logger;

/**
 * Akta 882 (Akta Perolehan Kerajaan 2026) -- CR SM40863.
 *
 * Suppliers still registered under business type 'K' (ROS - Pertubuhan,
 * Berdaftar dengan JPPM) or 'O' (Pertubuhan Lain-lain / Organization Others)
 * after the six-month transition window are deleted from eP.
 *
 * Deleting a supplier is NOT one UPDATE. It is three things, and they must
 * all happen or the account is left half-dead (record deleted but the users
 * can still log in): SM_SUPPLIER, SM_MOF_ACCOUNT, and per personnel
 * SM_PERSONNEL + PM_USER + PM_USER_ROLE + Liferay + OIM. The SO does all
 * three -- see skeleton file 3.
 *
 * Terminating in-flight applications is deliberately OUT OF SCOPE; skeleton
 * file 3 explains the consequence and the two ways to handle it.
 */
@Path("/sm")
public class AktaRosDeleteRest {

	public static final Logger log = Logger.getLogger(AktaRosDeleteRest.class);

	/**
	 * Report only. Lists who WOULD be deleted and writes nothing.
	 * Run this first, every time, in every environment.
	 */
	@GET
	@Path("akta882/rosdelete/dryrun")
	@Produces(MediaType.TEXT_PLAIN)
	public Response rosDeleteDryRun() {
		log.info("Before success calling rosDeleteDryRun rest api....");
		try {
			return Response.status(200).entity(deleteRosSuppliers(true)).build();
		} catch (Exception ex) {
			log.error("rosDeleteDryRun ERROR :: " + ex.getMessage(), ex);
			return Response.status(406).entity(ex.getMessage()).build();
		}
	}

	@GET
	@Path("akta882/rosdelete")
	@Produces(MediaType.TEXT_PLAIN)
	public Response rosDelete() {
		log.info("Before success calling rosDelete rest api....");
		try {
			return Response.status(200).entity(deleteRosSuppliers(false)).build();
		} catch (Exception ex) {
			log.error("rosDelete ERROR :: " + ex.getMessage(), ex);
			return Response.status(406).entity(ex.getMessage()).build();
		}
	}

	private String deleteRosSuppliers(boolean dryRun) {
		ISmSchedulerSO reqSO = SmServiceLookup.getSmSchedulerSO();
		SmSchedulerResDTO resDTO = reqSO.retrieveRosSupplierToDelete();
		List<SmSupplier> supplierLs = resDTO.getRosSupplierLs();

		log.info("**********deleteRosSuppliers (dryRun=" + dryRun + ") - Start************");
		log.info("List of ROS/Org supplier to delete == " + supplierLs.size());

		int deleted = 0;
		int failed = 0;


		for (SmSupplier supplier : supplierLs) {

			SmSchedulerReqDTO schedulerReqDTO = new SmSchedulerReqDTO();

			schedulerReqDTO.setSmSupplier(supplier);
			schedulerReqDTO.setSchedulerType(SupplierManagementConst.SM_SCHEDULER_A882_ROS_DELETE);
			schedulerReqDTO.setDryRun(dryRun);

			try {
				SmSchedulerResDTO one = reqSO.deleteRosSupplier(schedulerReqDTO);

				if (one.isSuccessful()) {
					deleted++;
				} else {
					failed++;
				}
			} catch (Exception ex) {
				failed++;
				log.info("deleteRosSupplier ERROR :: " + ex.getMessage() + " - supplier ID - "
						+ supplier.getSupplierId());
			}
		}

		String summary;
		if(dryRun){
			summary = "success calling rosDelete!! dryRun=true" + " total=" + supplierLs.size();
		}else{
			summary = "success calling rosDelete!! dryRun=false" + " total=" + supplierLs.size() + " deleted="
					+ deleted + " failed=" + failed;
		}

		log.info("**********deleteRosSuppliers - END************ " + summary);
		return summary;
	}
}
