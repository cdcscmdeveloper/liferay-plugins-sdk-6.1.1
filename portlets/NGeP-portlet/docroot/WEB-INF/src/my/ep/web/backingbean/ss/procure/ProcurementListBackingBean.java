package my.ep.web.backingbean.ss.procure;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;

import org.primefaces.event.RowEditEvent;

import my.ep.bpm.dto.BpmRequestDTO;
import my.ep.bpm.dto.BpmResponseDTO;
import my.ep.bpm.service.IBpmSO;
import my.ep.persistence.entity.pp.procurementplan.PpPlan;
import my.ep.util.lookupservice.EpBpmBusinessDelegate;
import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.bean.ss.quatationtender.QtnTenderBean;
import my.ep.web.managedbean.ss.procure.ProcureCodeManageBean;
/**
 * 
 * @author Tran Ngoc Hoa
 *
 */
@ManagedBean(name="procurementListBackingBean")
@RequestScoped
public class ProcurementListBackingBean extends BaseBackingBean<ProcureCodeManageBean>{
	public void createBPMTask(PpPlan ppPlan){
		//TODO call the Procurement plan SO to update procurement plan status before creating BPM task
		ppPlan.setPlanDesc("updated");
		IBpmSO bpmSO = (IBpmSO) EpBpmBusinessDelegate.getInstance().getServiceObject(IBpmSO.class);
		String ctx = bpmSO.authenticate("roslan","welcome1");
		BpmRequestDTO reqDTO = new BpmRequestDTO();
		reqDTO.setCtxToken(ctx);

		reqDTO.setProcessId("Quotation_Tender.DirectPurchaseCreation");
		reqDTO.setOutcome("");
		//  PSBudgetDataType obj = new PSBudgetDataType();
		//  obj.setApprover("Asdqwe");
		//  obj.setBudgetId("Asdqwe");
		//  obj.setPtj("Asdqwe");
		//  reqDTO.setPojo(obj);
		BpmResponseDTO res = bpmSO.initiateAndUpdateTask(reqDTO);
	}
	
	public void onEdit(RowEditEvent event) {
		PpPlan ppPlan	=	(PpPlan)event.getObject();
		//TODO call procurement plan to update notification date
	}
	public void test(){
		IBpmSO bpmSO = (IBpmSO) EpBpmBusinessDelegate.getInstance().getServiceObject(IBpmSO.class);
		String ctx = bpmSO.authenticate("roslan","welcome1");
		BpmRequestDTO reqDTO = new BpmRequestDTO();
		reqDTO.setCtxToken(ctx);

		reqDTO.setProcessId("Quotation_Tender.DirectPurchase");
		reqDTO.setOutcome("");
		//  PSBudgetDataType obj = new PSBudgetDataType();
		//  obj.setApprover("Asdqwe");
		//  obj.setBudgetId("Asdqwe");
		//  obj.setPtj("Asdqwe");
		//  reqDTO.setPojo(obj);
		BpmResponseDTO res = bpmSO.initiateAndUpdateTask(reqDTO);
	}
}
