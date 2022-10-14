package my.ep.rest.sm;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.faces.application.FacesMessage;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

import my.ep.constant.MessageConst;
import my.ep.dto.sm.personnel.PersonnelReqDTO;
import my.ep.dto.sm.personnel.PersonnelResDTO;
import my.ep.dto.sm.registration.RegistrationReqDTO;
import my.ep.dto.sm.registration.RegistrationResDTO;
import my.ep.dto.sm.supplierDataMaintenance.SupplierDataMaintenanceResDTO;
import my.ep.exception.EpException;
import my.ep.persistence.entity.pm.authorization.PmUser;
import my.ep.persistence.entity.pm.authorization.PmUserRole;
import my.ep.persistence.entity.pm.common.PmPendingRemoveTask;
import my.ep.persistence.entity.sm.SmDisciplinaryAction;
import my.ep.persistence.entity.sm.SmPersonnel;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.sm.helper.SmApplicationUtil;
import my.ep.web.util.sm.helper.SmCommonUtil;
import my.ep.web.util.sm.helper.SmServiceLookup;

import org.apache.log4j.Logger;

@Path("/sm") 
public class SmPendingRemoveTaskRest {

	public static Logger log = Logger.getLogger(DisciplinaryActionRest.class);

	@GET
	@Path("smpendingremovetask") 
	public Response updateAndDeletePendingTask() {	
		
		log.info("Before success calling retrieve rest api for sm pending delete task....");
		boolean enter  = true;
		try {
			
			PmPendingRemoveTask taskReassignmentToDelete = SmServiceLookup.getSupplierDataMaintenanceSO().getTaskReassignmentPmPendingRemoveTaskGroup("SM");
			if(taskReassignmentToDelete != null){
				SmServiceLookup.getSupplierDataMaintenanceSO().updateStatusToInProgress(taskReassignmentToDelete);
				SmServiceLookup.getSupplierDataMaintenanceSO().removeReassignAllGroupTasks("SM",taskReassignmentToDelete);
			}
			
			PmPendingRemoveTask taskReassignmentToDeleteOthers = SmServiceLookup.getSupplierDataMaintenanceSO().getTaskReassignmentPmPendingRemoveTaskOthers("SM");
			if(taskReassignmentToDeleteOthers != null){
				SmServiceLookup.getSupplierDataMaintenanceSO().updateStatusToInProgress(taskReassignmentToDeleteOthers);
				SmServiceLookup.getSupplierDataMaintenanceSO().removeReassignAllOthersTasks("SM",taskReassignmentToDeleteOthers);
			}
			return Response.status(200).entity("success calling process sm pending delete task !!").build();
		} catch (Exception e) {
			return Response.status(406).entity(e.getMessage()).build();
		}
	}
}
